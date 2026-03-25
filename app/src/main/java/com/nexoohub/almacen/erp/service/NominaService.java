package com.nexoohub.almacen.erp.service;

import com.nexoohub.almacen.common.exception.ResourceNotFoundException;
import com.nexoohub.almacen.erp.dto.*;
import com.nexoohub.almacen.erp.entity.*;
import com.nexoohub.almacen.erp.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NominaService {

    private static final Logger log = LoggerFactory.getLogger(NominaService.class);

    private final ErpEmpleadoRepository empleadoRepo;
    private final NominaPeriodoRepository periodoRepo;
    private final ReciboNominaRepository reciboRepo;

    public NominaService(ErpEmpleadoRepository empleadoRepo,
                         NominaPeriodoRepository periodoRepo,
                         ReciboNominaRepository reciboRepo) {
        this.empleadoRepo = empleadoRepo;
        this.periodoRepo = periodoRepo;
        this.reciboRepo = reciboRepo;
    }

    // ==========================================
    // EMPLEADOS
    // ==========================================

    @Transactional
    public EmpleadoDTO registrarEmpleado(EmpleadoDTO req) {
        log.info("Registrando empleado: {}", req.getNombreCompleto());
        if (req.getRfc() != null && empleadoRepo.findByRfc(req.getRfc()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un empleado con RFC: " + req.getRfc());
        }

        Empleado emp = new Empleado();
        emp.setUsuarioId(req.getUsuarioId());
        emp.setSucursalId(req.getSucursalId());
        emp.setNombreCompleto(req.getNombreCompleto());
        emp.setCurp(req.getCurp());
        emp.setRfc(req.getRfc());
        emp.setNss(req.getNss());
        emp.setDepartamento(req.getDepartamento());
        emp.setPuesto(req.getPuesto());
        emp.setSalarioDiario(req.getSalarioDiario() != null ? req.getSalarioDiario() : BigDecimal.ZERO);
        emp.setFechaIngreso(req.getFechaIngreso() != null ? req.getFechaIngreso() : LocalDate.now());
        emp.setEstatus(req.getEstatus() != null ? req.getEstatus().toUpperCase() : "ACTIVO");

        return mapToEmpleadoDTO(empleadoRepo.save(emp));
    }

    @Transactional(readOnly = true)
    public List<EmpleadoDTO> listarEmpleados(Integer sucursalId) {
        return empleadoRepo.findBySucursalIdAndEstatus(sucursalId, "ACTIVO")
                .stream().map(this::mapToEmpleadoDTO).collect(Collectors.toList());
    }

    // ==========================================
    // PERIODOS
    // ==========================================

    @Transactional
    public NominaPeriodoResponse crearPeriodo(NominaPeriodoRequest req) {
        log.info("Creando periodo de nómina: {}", req.getNombre());

        if (req.getFechaFin().isBefore(req.getFechaInicio())) {
            throw new IllegalArgumentException("La fecha de fin no puede ser menor a la de inicio");
        }

        NominaPeriodo p = new NominaPeriodo();
        p.setNombre(req.getNombre());
        p.setFechaInicio(req.getFechaInicio());
        p.setFechaFin(req.getFechaFin());
        p.setTipoPeriodo(req.getTipoPeriodo().toUpperCase());
        p.setUsuarioId(req.getUsuarioId());

        return mapToPeriodoResponse(periodoRepo.save(p));
    }

    @Transactional(readOnly = true)
    public List<NominaPeriodoResponse> listarPeriodos(LocalDate desde, LocalDate hasta) {
        return periodoRepo.findByFechaInicioGreaterThanEqualAndFechaFinLessThanEqualOrderByFechaInicioDesc(desde, hasta)
                .stream().map(this::mapToPeriodoResponse).collect(Collectors.toList());
    }

    // ==========================================
    // CÁLCULO DE RECIBOS AUTOMÁTICO
    // ==========================================

    @Transactional
    public NominaPeriodoResponse generarRecibosParaPeriodo(Integer periodoId, Integer sucursalId) {
        log.info("Generando recibos del periodo: {} para sucursal: {}", periodoId, sucursalId);
        
        NominaPeriodo periodo = periodoRepo.findById(periodoId)
                .orElseThrow(() -> new ResourceNotFoundException("Periodo no encontrado"));

        if (!"BORRADOR".equals(periodo.getEstatus())) {
            throw new IllegalStateException("Solo se pueden generar recibos para periodos en BORRADOR");
        }

        List<Empleado> empleados = empleadoRepo.findBySucursalIdAndEstatus(sucursalId, "ACTIVO");

        int generados = 0;
        for (Empleado emp : empleados) {
            // Verificar si ya tiene recibo
            if (reciboRepo.findByPeriodoIdAndEmpleadoId(periodo.getId(), emp.getId()).isEmpty()) {
                ReciboNomina recibo = calcularReciboAutomatico(periodo, emp);
                periodo.addRecibo(recibo);
                generados++;
            }
        }
        
        log.info("Nómina calculada. Recibos generados: {}", generados);
        return mapToPeriodoResponse(periodoRepo.save(periodo));
    }

    private ReciboNomina calcularReciboAutomatico(NominaPeriodo periodo, Empleado empleado) {
        ReciboNomina rn = new ReciboNomina();
        rn.setEmpleado(empleado);
        
        // Dependiendo del tipo calculamos los días. Aquí usaremos un estándar simple (Quincenal=15, Semanal=7, Mensual=30)
        BigDecimal dias = switch (periodo.getTipoPeriodo()) {
            case "SEMANAL" -> new BigDecimal("7.00");
            case "MENSUAL" -> new BigDecimal("30.00");
            default -> new BigDecimal("15.00");
        };
        rn.setDiasTrabajados(dias);

        // -- PERCEPCIONES --
        // Sueldo Base
        BigDecimal sueldoBase = empleado.getSalarioDiario().multiply(dias);
        ReciboNominaDetalle detSueldo = new ReciboNominaDetalle();
        detSueldo.setTipoConcepto("PERCEPCION");
        detSueldo.setClaveSat("001");
        detSueldo.setDescripcion("Sueldo Base");
        detSueldo.setImporte(sueldoBase);
        rn.addDetalle(detSueldo);

        // -- DEDUCCIONES --
        // ISR (Proxy simple 10% del sueldo)
        BigDecimal isr = sueldoBase.multiply(new BigDecimal("0.10"));
        ReciboNominaDetalle detIsr = new ReciboNominaDetalle();
        detIsr.setTipoConcepto("DEDUCCION");
        detIsr.setClaveSat("002");
        detIsr.setDescripcion("ISR Retenido (Proxy)");
        detIsr.setImporte(isr);
        rn.addDetalle(detIsr);

        // IMSS (Proxy simple 2% del sueldo)
        BigDecimal imss = sueldoBase.multiply(new BigDecimal("0.02"));
        ReciboNominaDetalle detImss = new ReciboNominaDetalle();
        detImss.setTipoConcepto("DEDUCCION");
        detImss.setClaveSat("001");
        detImss.setDescripcion("Aportación IMSS (Proxy)");
        detImss.setImporte(imss);
        rn.addDetalle(detImss);

        // El addDetalle recalculará internamente Totales y NetoPagar.
        return rn;
    }

    @Transactional(readOnly = true)
    public ReciboNominaResponse obtenerRecibo(Integer reciboId) {
        return reciboRepo.findById(reciboId)
                .map(this::mapToReciboResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Recibo no encontrado"));
    }

    // ==========================================
    // MAPPERS
    // ==========================================

    private EmpleadoDTO mapToEmpleadoDTO(Empleado e) {
        EmpleadoDTO d = new EmpleadoDTO();
        d.setId(e.getId());
        d.setUsuarioId(e.getUsuarioId());
        d.setSucursalId(e.getSucursalId());
        d.setNombreCompleto(e.getNombreCompleto());
        d.setCurp(e.getCurp());
        d.setRfc(e.getRfc());
        d.setNss(e.getNss());
        d.setDepartamento(e.getDepartamento());
        d.setPuesto(e.getPuesto());
        d.setSalarioDiario(e.getSalarioDiario());
        d.setFechaIngreso(e.getFechaIngreso());
        d.setEstatus(e.getEstatus());
        d.setCreatedAt(e.getCreatedAt());
        return d;
    }

    private NominaPeriodoResponse mapToPeriodoResponse(NominaPeriodo p) {
        NominaPeriodoResponse r = new NominaPeriodoResponse();
        r.setId(p.getId());
        r.setNombre(p.getNombre());
        r.setFechaInicio(p.getFechaInicio());
        r.setFechaFin(p.getFechaFin());
        r.setTipoPeriodo(p.getTipoPeriodo());
        r.setEstatus(p.getEstatus());
        r.setUsuarioId(p.getUsuarioId());
        r.setCreatedAt(p.getCreatedAt());
        r.setCantidadRecibosGenerados(p.getRecibos().size());
        return r;
    }

    private ReciboNominaResponse mapToReciboResponse(ReciboNomina r) {
        ReciboNominaResponse resp = new ReciboNominaResponse();
        resp.setId(r.getId());
        resp.setPeriodoId(r.getPeriodo().getId());
        resp.setEmpleado(mapToEmpleadoDTO(r.getEmpleado()));
        resp.setDiasTrabajados(r.getDiasTrabajados());
        resp.setTotalPercepciones(r.getTotalPercepciones());
        resp.setTotalDeducciones(r.getTotalDeducciones());
        resp.setNetoPagar(r.getNetoPagar());
        resp.setMetodoPago(r.getMetodoPago());
        resp.setCreatedAt(r.getCreatedAt());
        
        List<ReciboNominaDetalleDTO> conceptos = r.getDetalles().stream().map(d -> {
            ReciboNominaDetalleDTO dto = new ReciboNominaDetalleDTO();
            dto.setId(d.getId());
            dto.setTipoConcepto(d.getTipoConcepto());
            dto.setClaveSat(d.getClaveSat());
            dto.setDescripcion(d.getDescripcion());
            dto.setImporte(d.getImporte());
            dto.setCreatedAt(d.getCreatedAt());
            return dto;
        }).collect(Collectors.toList());
        resp.setConceptos(conceptos);
        
        return resp;
    }
}
