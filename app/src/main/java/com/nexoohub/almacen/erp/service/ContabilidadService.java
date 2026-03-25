package com.nexoohub.almacen.erp.service;

import com.nexoohub.almacen.common.exception.ResourceNotFoundException;
import com.nexoohub.almacen.erp.dto.*;
import com.nexoohub.almacen.erp.dto.reportes.*;
import com.nexoohub.almacen.erp.entity.CuentaContable;
import com.nexoohub.almacen.erp.entity.MovimientoContable;
import com.nexoohub.almacen.erp.entity.PolizaContable;
import com.nexoohub.almacen.erp.repository.CuentaContableRepository;
import com.nexoohub.almacen.erp.repository.MovimientoContableRepository;
import com.nexoohub.almacen.erp.repository.PolizaContableRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ContabilidadService {

    private static final Logger log = LoggerFactory.getLogger(ContabilidadService.class);

    private final CuentaContableRepository cuentaRepo;
    private final PolizaContableRepository polizaRepo;
    private final MovimientoContableRepository movimientoRepo;

    public ContabilidadService(CuentaContableRepository cuentaRepo,
                               PolizaContableRepository polizaRepo,
                               MovimientoContableRepository movimientoRepo) {
        this.cuentaRepo = cuentaRepo;
        this.polizaRepo = polizaRepo;
        this.movimientoRepo = movimientoRepo;
    }

    @Transactional(readOnly = true)
    public List<CuentaContableDTO> listarCuentas() {
        return cuentaRepo.findByActivaTrueOrderByCodigoAsc().stream()
                .map(this::mapCuentaToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public PolizaContableResponse registrarPoliza(PolizaContableRequest req) {
        log.info("Registrando póliza {} tipo {}", req.getNumeroPoliza(), req.getTipoPoliza());

        if (polizaRepo.findByNumeroPoliza(req.getNumeroPoliza()).isPresent()) {
            throw new IllegalArgumentException("El número de póliza ya existe: " + req.getNumeroPoliza());
        }

        BigDecimal totalCargo = BigDecimal.ZERO;
        BigDecimal totalAbono = BigDecimal.ZERO;

        PolizaContable poliza = new PolizaContable();
        poliza.setNumeroPoliza(req.getNumeroPoliza());
        poliza.setFecha(req.getFecha());
        poliza.setTipoPoliza(req.getTipoPoliza().toUpperCase());
        poliza.setConcepto(req.getConcepto());
        poliza.setUsuarioId(req.getUsuarioId());

        for (MovimientoContableRequest mReq : req.getMovimientos()) {
            CuentaContable cuenta = cuentaRepo.findById(mReq.getCuentaId())
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada: " + mReq.getCuentaId()));

            if (!cuenta.getActiva()) {
                throw new IllegalStateException("La cuenta " + cuenta.getCodigo() + " está inactiva.");
            }

            MovimientoContable mov = new MovimientoContable();
            mov.setCuentaId(cuenta.getId());
            mov.setConceptoDetalle(mReq.getConceptoDetalle());
            mov.setCargo(mReq.getCargo() != null ? mReq.getCargo() : BigDecimal.ZERO);
            mov.setAbono(mReq.getAbono() != null ? mReq.getAbono() : BigDecimal.ZERO);

            totalCargo = totalCargo.add(mov.getCargo());
            totalAbono = totalAbono.add(mov.getAbono());

            poliza.addMovimiento(mov);
        }

        if (totalCargo.compareTo(totalAbono) != 0) {
            throw new IllegalStateException(String.format("Partida doble no cuadra. Cargos: %s, Abonos: %s", totalCargo, totalAbono));
        }

        poliza.setTotalCargo(totalCargo);
        poliza.setTotalAbono(totalAbono);

        return mapPolizaToResponse(polizaRepo.save(poliza));
    }

    @Transactional(readOnly = true)
    public List<PolizaContableResponse> listarPolizas(LocalDate desde, LocalDate hasta) {
        return polizaRepo.findByFechaBetweenOrderByFechaDesc(desde, hasta).stream()
                .map(this::mapPolizaToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BalanzaComprobacionResponse generarBalanza(LocalDate desde, LocalDate hasta) {
        List<CuentaContable> cuentas = cuentaRepo.findByActivaTrueOrderByCodigoAsc();
        List<MovimientoContable> movimientos = movimientoRepo.findMovimientosPorFecha(desde, hasta);

        // Agrupar movimientos por cuenta
        Map<Integer, List<MovimientoContable>> movsPorCuenta = movimientos.stream()
                .collect(Collectors.groupingBy(MovimientoContable::getCuentaId));

        BalanzaComprobacionResponse balanza = new BalanzaComprobacionResponse();
        balanza.setPeriodo(desde + " al " + hasta);
        List<CuentaBalanzaDTO> lineas = new ArrayList<>();

        BigDecimal granTotalCargo = BigDecimal.ZERO;
        BigDecimal granTotalAbono = BigDecimal.ZERO;

        for (CuentaContable c : cuentas) {
            List<MovimientoContable> movs = movsPorCuenta.getOrDefault(c.getId(), new ArrayList<>());
            BigDecimal sumCargo = movs.stream().map(MovimientoContable::getCargo).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal sumAbono = movs.stream().map(MovimientoContable::getAbono).reduce(BigDecimal.ZERO, BigDecimal::add);

            if (sumCargo.compareTo(BigDecimal.ZERO) == 0 && sumAbono.compareTo(BigDecimal.ZERO) == 0) {
                continue; // Omitir cuentas sin movimientos en el periodo
            }

            CuentaBalanzaDTO dto = new CuentaBalanzaDTO();
            dto.setCodigo(c.getCodigo());
            dto.setNombre(c.getNombre());
            dto.setNaturaleza(c.getNaturaleza());
            dto.setMovimientosCargo(sumCargo);
            dto.setMovimientosAbono(sumAbono);

            // Calcular saldo final según naturaleza
            BigDecimal saldoFinal = BigDecimal.ZERO;
            if ("DEUDORA".equals(c.getNaturaleza())) {
                saldoFinal = sumCargo.subtract(sumAbono);
            } else {
                saldoFinal = sumAbono.subtract(sumCargo);
            }
            dto.setSaldoFinal(saldoFinal);

            lineas.add(dto);

            granTotalCargo = granTotalCargo.add(sumCargo);
            granTotalAbono = granTotalAbono.add(sumAbono);
        }

        balanza.setCuentas(lineas);
        balanza.setTotalCargos(granTotalCargo);
        balanza.setTotalAbonos(granTotalAbono);
        balanza.setCuadrada(granTotalCargo.compareTo(granTotalAbono) == 0);

        return balanza;
    }

    @Transactional(readOnly = true)
    public EstadoResultadosResponse generarEstadoResultados(LocalDate desde, LocalDate hasta) {
        List<MovimientoContable> movimientos = movimientoRepo.findMovimientosPorFecha(desde, hasta);
        
        // Sumarizar por tipo (Ingresos 400, Costos 500, Gastos 600)
        // Para simplificar asuminos que cuenta inicia con ese prefijo (como en V16)
        BigDecimal ingresos = BigDecimal.ZERO;
        BigDecimal costos = BigDecimal.ZERO;
        BigDecimal gastos = BigDecimal.ZERO;

        for (MovimientoContable m : movimientos) {
            CuentaContable c = cuentaRepo.findById(m.getCuentaId()).orElse(null);
            if (c == null) continue;

            // Ingresos son ACREEDORA (suelen abonarse)
            if (c.getCodigo().startsWith("4")) {
                ingresos = ingresos.add(m.getAbono()).subtract(m.getCargo());
            }
            // Costos son DEUDORA (suelen cargarse)
            else if (c.getCodigo().startsWith("5")) {
                costos = costos.add(m.getCargo()).subtract(m.getAbono());
            }
            // Gastos son DEUDORA (suelen cargarse)
            else if (c.getCodigo().startsWith("6")) {
                gastos = gastos.add(m.getCargo()).subtract(m.getAbono());
            }
        }

        BigDecimal utilidadBruta = ingresos.subtract(costos);
        BigDecimal utilidadNeta = utilidadBruta.subtract(gastos);

        EstadoResultadosResponse dto = new EstadoResultadosResponse();
        dto.setPeriodo(desde + " al " + hasta);
        dto.setIngresosNetos(ingresos);
        dto.setCostoVentas(costos);
        dto.setUtilidadBruta(utilidadBruta);
        dto.setGastosOperacion(gastos);
        dto.setUtilidadNeta(utilidadNeta);

        return dto;
    }

    // Mappers
    private CuentaContableDTO mapCuentaToDTO(CuentaContable e) {
        CuentaContableDTO d = new CuentaContableDTO();
        d.setId(e.getId());
        d.setCodigo(e.getCodigo());
        d.setNombre(e.getNombre());
        d.setTipoCuenta(e.getTipoCuenta());
        d.setNaturaleza(e.getNaturaleza());
        d.setNivel(e.getNivel());
        d.setCuentaPadreId(e.getCuentaPadreId());
        d.setActiva(e.getActiva());
        return d;
    }

    private PolizaContableResponse mapPolizaToResponse(PolizaContable e) {
        PolizaContableResponse d = new PolizaContableResponse();
        d.setId(e.getId());
        d.setNumeroPoliza(e.getNumeroPoliza());
        d.setFecha(e.getFecha());
        d.setTipoPoliza(e.getTipoPoliza());
        d.setConcepto(e.getConcepto());
        d.setTotalCargo(e.getTotalCargo());
        d.setTotalAbono(e.getTotalAbono());
        d.setEstatus(e.getEstatus());

        List<MovimientoContableDTO> movs = e.getMovimientos().stream().map(m -> {
            MovimientoContableDTO md = new MovimientoContableDTO();
            md.setCuentaId(m.getCuentaId());
            md.setConceptoDetalle(m.getConceptoDetalle());
            md.setCargo(m.getCargo());
            md.setAbono(m.getAbono());
            cuentaRepo.findById(m.getCuentaId()).ifPresent(c -> {
                md.setCodigoCuenta(c.getCodigo());
                md.setNombreCuenta(c.getNombre());
            });
            return md;
        }).collect(Collectors.toList());
        d.setMovimientos(movs);

        return d;
    }
}
