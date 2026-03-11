package com.nexoohub.almacen.comisiones.service;

import com.nexoohub.almacen.comisiones.dto.*;
import com.nexoohub.almacen.comisiones.entity.Comision;
import com.nexoohub.almacen.comisiones.entity.ReglaComision;
import com.nexoohub.almacen.comisiones.mapper.ComisionMapper;
import com.nexoohub.almacen.comisiones.mapper.ReglaComisionMapper;
import com.nexoohub.almacen.comisiones.repository.ComisionRepository;
import com.nexoohub.almacen.comisiones.repository.ReglaComisionRepository;
import com.nexoohub.almacen.empleados.entity.Empleado;
import com.nexoohub.almacen.empleados.repository.EmpleadoRepository;
import com.nexoohub.almacen.ventas.entity.Venta;
import com.nexoohub.almacen.ventas.repository.VentaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio para gestionar comisiones de vendedores.
 * Implementa el cálculo automático de comisiones basado en reglas configurables.
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
@Service
public class ComisionService {

    private final ComisionRepository comisionRepository;
    private final ReglaComisionRepository reglaComisionRepository;
    private final VentaRepository ventaRepository;
    private final EmpleadoRepository empleadoRepository;
    private final ComisionMapper comisionMapper;
    private final ReglaComisionMapper reglaComisionMapper;

    public ComisionService(
            ComisionRepository comisionRepository,
            ReglaComisionRepository reglaComisionRepository,
            VentaRepository ventaRepository,
            EmpleadoRepository empleadoRepository,
            ComisionMapper comisionMapper,
            ReglaComisionMapper reglaComisionMapper) {
        this.comisionRepository = comisionRepository;
        this.reglaComisionRepository = reglaComisionRepository;
        this.ventaRepository = ventaRepository;
        this.empleadoRepository = empleadoRepository;
        this.comisionMapper = comisionMapper;
        this.reglaComisionMapper = reglaComisionMapper;
    }

    // ==========================================
    // GESTIÓN DE REGLAS
    // ==========================================

    /**
     * Crea una nueva regla de comisión
     */
    @Transactional
    public ReglaComisionResponseDTO crearRegla(ReglaComisionRequestDTO dto) {
        ReglaComision regla = reglaComisionMapper.toEntity(dto);
        regla = reglaComisionRepository.save(regla);
        return reglaComisionMapper.toDTO(regla);
    }

    /**
     * Actualiza una regla existente
     */
    @Transactional
    public ReglaComisionResponseDTO actualizarRegla(Integer id, ReglaComisionRequestDTO dto) {
        ReglaComision regla = reglaComisionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Regla de comisión no encontrada"));
        
        reglaComisionMapper.updateEntity(regla, dto);
        regla = reglaComisionRepository.save(regla);
        return reglaComisionMapper.toDTO(regla);
    }

    /**
     * Obtiene una regla por ID
     */
    @Transactional(readOnly = true)
    public ReglaComisionResponseDTO obtenerReglaPorId(Integer id) {
        ReglaComision regla = reglaComisionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Regla de comisión no encontrada"));
        return reglaComisionMapper.toDTO(regla);
    }

    /**
     * Lista todas las reglas activas
     */
    @Transactional(readOnly = true)
    public List<ReglaComisionResponseDTO> listarReglasActivas() {
        return reglaComisionRepository.findAllActivas().stream()
                .map(reglaComisionMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lista todas las reglas (activas e inactivas)
     */
    @Transactional(readOnly = true)
    public List<ReglaComisionResponseDTO> listarTodasLasReglas() {
        return reglaComisionRepository.findAll().stream()
                .map(reglaComisionMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Elimina una regla (soft delete - la marca como inactiva)
     */
    @Transactional
    public void eliminarRegla(Integer id) {
        ReglaComision regla = reglaComisionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Regla de comisión no encontrada"));
        regla.setActiva(false);
        reglaComisionRepository.save(regla);
    }

    // ==========================================
    // CÁLCULO DE COMISIONES
    // ==========================================

    /**
     * Calcula las comisiones para todos los vendedores en el periodo especificado.
     * Si ya existen comisiones, las recalcula.
     */
    @Transactional
    public List<ComisionResponseDTO> calcularComisionesPorPeriodo(Integer anio, Integer mes) {
        // Obtener todos los vendedores activos
        List<Empleado> vendedores = empleadoRepository.findAll().stream()
                .filter(Empleado::getActivo)
                .collect(Collectors.toList());

        List<ComisionResponseDTO> comisiones = vendedores.stream()
                .map(vendedor -> calcularComisionVendedor(vendedor, anio, mes))
                .collect(Collectors.toList());

        return comisiones;
    }

    /**
     * Calcula o recalcula la comisión de un vendedor específico en un periodo
     */
    @Transactional
    public ComisionResponseDTO calcularComisionVendedor(Integer vendedorId, Integer anio, Integer mes) {
        Empleado vendedor = empleadoRepository.findById(vendedorId)
                .orElseThrow(() -> new EntityNotFoundException("Vendedor no encontrado"));
        
        return calcularComisionVendedor(vendedor, anio, mes);
    }

    private ComisionResponseDTO calcularComisionVendedor(Empleado vendedor, Integer anio, Integer mes) {
        // Buscar si ya existe una comisión para este periodo
        Optional<Comision> comisionExistente = comisionRepository
                .findByVendedorIdAndPeriodoAnioAndPeriodoMes(vendedor.getId(), anio, mes);

        // Calcular rango de fechas del periodo
        YearMonth yearMonth = YearMonth.of(anio, mes);
        LocalDateTime inicioMes = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime finMes = yearMonth.atEndOfMonth().atTime(23, 59, 59);

        // Obtener ventas del vendedor en el periodo
        List<Venta> ventas = ventaRepository.findByVendedorIdAndFechaVentaBetween(
                vendedor.getId(), inicioMes, finMes);

        // Calcular totales
        BigDecimal totalVentas = ventas.stream()
                .map(Venta::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int cantidadVentas = ventas.size();

        // Buscar reglas aplicables al vendedor
        List<ReglaComision> reglasAplicables = reglaComisionRepository.findByPuesto(vendedor.getPuesto());

        // Calcular comisión base y bonos
        BigDecimal comisionBase = calcularComisionBase(reglasAplicables, totalVentas, ventas);
        BigDecimal bonos = calcularBonos(reglasAplicables, totalVentas);

        // Crear o actualizar comisión
        Comision comision;
        if (comisionExistente.isPresent()) {
            comision = comisionExistente.get();
            // Solo actualizar si está PENDIENTE
            if (!"PENDIENTE".equals(comision.getEstado())) {
                throw new IllegalStateException(
                    "No se puede recalcular una comisión que ya fue aprobada o pagada");
            }
        } else {
            comision = new Comision();
            comision.setVendedor(vendedor);
            comision.setVendedorId(vendedor.getId());
            comision.setPeriodoAnio(anio);
            comision.setPeriodoMes(mes);
            comision.setEstado("PENDIENTE");
        }

        comision.setTotalVentas(totalVentas);
        comision.setCantidadVentas(cantidadVentas);
        comision.setComisionBase(comisionBase);
        comision.setBonos(bonos);
        comision.calcularTotal();

        comision = comisionRepository.save(comision);
        return comisionMapper.toDTO(comision);
    }

    /**
     * Calcula la comisión base aplicando todas las reglas de tipo PORCENTAJE_VENTA
     */
    private BigDecimal calcularComisionBase(List<ReglaComision> reglas, 
                                           BigDecimal totalVentas, 
                                           List<Venta> ventas) {
        BigDecimal comisionTotal = BigDecimal.ZERO;

        for (ReglaComision regla : reglas) {
            if (!regla.getActiva()) continue;

            switch (regla.getTipo()) {
                case "PORCENTAJE_VENTA":
                    if (regla.getPorcentajeComision() != null) {
                        BigDecimal comision = totalVentas
                                .multiply(regla.getPorcentajeComision())
                                .setScale(2, RoundingMode.HALF_UP);
                        comisionTotal = comisionTotal.add(comision);
                    }
                    break;

                case "MONTO_FIJO":
                    if (regla.getMontoFijo() != null) {
                        comisionTotal = comisionTotal.add(regla.getMontoFijo());
                    }
                    break;

                case "POR_PRODUCTO":
                    // Calcular comisión por productos específicos
                    if (regla.getSkuProducto() != null && regla.getPorcentajeComision() != null) {
                        BigDecimal comisionProducto = calcularComisionPorProducto(
                                ventas, regla.getSkuProducto(), regla.getPorcentajeComision());
                        comisionTotal = comisionTotal.add(comisionProducto);
                    }
                    break;
            }
        }

        return comisionTotal;
    }

    /**
     * Calcula comisión adicional por ventas de un producto específico
     */
    private BigDecimal calcularComisionPorProducto(List<Venta> ventas, 
                                                   String skuProducto, 
                                                   BigDecimal porcentaje) {
        BigDecimal totalProducto = ventas.stream()
                .flatMap(venta -> venta.getDetalles().stream())
                .filter(detalle -> skuProducto.equals(detalle.getSkuInterno()))
                .map(detalle -> detalle.getPrecioUnitarioVenta()
                        .multiply(new BigDecimal(detalle.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return totalProducto.multiply(porcentaje).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calcula bonos por cumplimiento de metas
     */
    private BigDecimal calcularBonos(List<ReglaComision> reglas, BigDecimal totalVentas) {
        BigDecimal bonosTotal = BigDecimal.ZERO;

        for (ReglaComision regla : reglas) {
            if (!regla.getActiva()) continue;

            if ("POR_META".equals(regla.getTipo()) && regla.getMetaMensual() != null) {
                // Si alcanzó o superó la meta, recibe el bono
                if (totalVentas.compareTo(regla.getMetaMensual()) >= 0) {
                    bonosTotal = bonosTotal.add(regla.getBonoMeta() != null 
                            ? regla.getBonoMeta() : BigDecimal.ZERO);
                }
            }
        }

        return bonosTotal;
    }

    // ==========================================
    // CONSULTA DE COMISIONES
    // ==========================================

    /**
     * Obtiene una comisión por ID
     */
    @Transactional(readOnly = true)
    public ComisionResponseDTO obtenerComisionPorId(Integer id) {
        Comision comision = comisionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comisión no encontrada"));
        return comisionMapper.toDTO(comision);
    }

    /**
     * Lista comisiones de un vendedor
     */
    @Transactional(readOnly = true)
    public List<ComisionResponseDTO> listarComisionesPorVendedor(Integer vendedorId) {
        return comisionRepository.findByVendedorIdOrderByPeriodoAnioDescPeriodoMesDesc(vendedorId)
                .stream()
                .map(comisionMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lista comisiones por periodo
     */
    @Transactional(readOnly = true)
    public List<ComisionResponseDTO> listarComisionesPorPeriodo(Integer anio, Integer mes) {
        return comisionRepository.findByPeriodoAnioAndPeriodoMesOrderByVendedorId(anio, mes)
                .stream()
                .map(comisionMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lista comisiones por estado
     */
    @Transactional(readOnly = true)
    public List<ComisionResponseDTO> listarComisionesPorEstado(String estado) {
        return comisionRepository.findByEstadoOrderByPeriodoAnioDescPeriodoMesDesc(estado)
                .stream()
                .map(comisionMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene resumen de comisiones por periodo
     */
    @Transactional(readOnly = true)
    public ResumenComisionesDTO obtenerResumenPorPeriodo(Integer anio, Integer mes) {
        List<ComisionResponseDTO> comisiones = listarComisionesPorPeriodo(anio, mes);

        ResumenComisionesDTO resumen = new ResumenComisionesDTO();
        
        // Formatear periodo
        Month month = Month.of(mes);
        String nombreMes = month.getDisplayName(TextStyle.FULL, Locale.of("es", "MX"));
        resumen.setPeriodo(nombreMes.substring(0, 1).toUpperCase() + nombreMes.substring(1) + " " + anio);

        // Calcular totales
        resumen.setCantidadVendedores(comisiones.size());
        resumen.setTotalComisiones(comisiones.stream()
                .map(ComisionResponseDTO::getTotalComision)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        resumen.setTotalVentas(comisiones.stream()
                .map(ComisionResponseDTO::getTotalVentas)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        // Contar por estado
        resumen.setCantidadComisionesPendientes(
                (int) comisiones.stream().filter(c -> "PENDIENTE".equals(c.getEstado())).count());
        resumen.setCantidadComisionesAprobadas(
                (int) comisiones.stream().filter(c -> "APROBADA".equals(c.getEstado())).count());
        resumen.setCantidadComisionesPagadas(
                (int) comisiones.stream().filter(c -> "PAGADA".equals(c.getEstado())).count());

        resumen.setDetalles(comisiones);

        return resumen;
    }

    // ==========================================
    // APROBACIÓN Y PAGO
    // ==========================================

    /**
     * Aprueba o rechaza una comisión
     */
    @Transactional
    public ComisionResponseDTO aprobarComision(Integer id, AprobarComisionRequestDTO dto) {
        Comision comision = comisionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comisión no encontrada"));

        if (!"PENDIENTE".equals(comision.getEstado())) {
            throw new IllegalStateException("Solo se pueden aprobar/rechazar comisiones en estado PENDIENTE");
        }

        comision.setEstado(dto.getNuevoEstado());
        
        if ("APROBADA".equals(dto.getNuevoEstado())) {
            comision.setFechaAprobacion(LocalDate.now());
        }

        if (dto.getNotas() != null) {
            comision.setNotas(dto.getNotas());
        }

        // TODO: Obtener usuario autenticado del SecurityContext
        comision.setUsuarioAprobador("admin"); // Placeholder

        comision = comisionRepository.save(comision);
        return comisionMapper.toDTO(comision);
    }

    /**
     * Marca una comisión como pagada
     */
    @Transactional
    public ComisionResponseDTO marcarComoPagada(Integer id) {
        Comision comision = comisionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comisión no encontrada"));

        if (!"APROBADA".equals(comision.getEstado())) {
            throw new IllegalStateException("Solo se pueden marcar como pagadas las comisiones APROBADAS");
        }

        comision.setEstado("PAGADA");
        comision.setFechaPago(LocalDate.now());

        comision = comisionRepository.save(comision);
        return comisionMapper.toDTO(comision);
    }

    /**
     * Permite ajustar manualmente una comisión (agregar o quitar monto)
     */
    @Transactional
    public ComisionResponseDTO ajustarComision(Integer id, BigDecimal ajuste, String motivo) {
        Comision comision = comisionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comisión no encontrada"));

        if (!"PENDIENTE".equals(comision.getEstado())) {
            throw new IllegalStateException("Solo se pueden ajustar comisiones en estado PENDIENTE");
        }

        comision.setAjustes(ajuste);
        comision.setNotas(motivo);
        comision.calcularTotal();

        comision = comisionRepository.save(comision);
        return comisionMapper.toDTO(comision);
    }
}
