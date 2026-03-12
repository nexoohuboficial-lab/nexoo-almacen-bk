package com.nexoohub.almacen.inventario.service;

import com.nexoohub.almacen.common.exception.ResourceNotFoundException;
import com.nexoohub.almacen.inventario.dto.*;
import com.nexoohub.almacen.inventario.entity.AlertaLentoMovimiento;
import com.nexoohub.almacen.inventario.repository.AlertaLentoMovimientoRepository;
import com.nexoohub.almacen.sucursal.entity.Sucursal;
import com.nexoohub.almacen.sucursal.repository.SucursalRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio para gestión de alertas de productos de lento movimiento.
 * 
 * <p>Este servicio implementa la lógica de negocio para:</p>
 * <ul>
 *   <li>Detectar automáticamente productos sin ventas en X días</li>
 *   <li>Generar y actualizar alertas con niveles de criticidad</li>
 *   <li>Calcular costos de inventario inmovilizado</li>
 *   <li>Proporcionar sugerencias de acciones correctivas</li>
 *   <li>Gestionar resolución de alertas</li>
 * </ul>
 * 
 * <p><b>Niveles de Alerta:</b></p>
 * <ul>
 *   <li>ADVERTENCIA: 30-60 días sin venta</li>
 *   <li>CRITICO: más de 60 días sin venta</li>
 * </ul>
 * 
 * @author NexooHub Development Team
 * @since 1.2.0
 */
@Service
@Transactional
@Slf4j
public class AlertaLentoMovimientoService {

    private final AlertaLentoMovimientoRepository alertaRepository;
    private final SucursalRepository sucursalRepository;

    public AlertaLentoMovimientoService(
            AlertaLentoMovimientoRepository alertaRepository,
            SucursalRepository sucursalRepository) {
        this.alertaRepository = alertaRepository;
        this.sucursalRepository = sucursalRepository;
    }

    /**
     * Genera alertas de productos de lento movimiento.
     * 
     * <p>Detecta productos sin ventas en los últimos N días y crea/actualiza alertas.</p>
     * 
     * @param request Configuración para generación (umbral de días, sucursal)
     * @return Lista de alertas generadas/actualizadas
     */
    public List<AlertaLentoMovimientoResponseDTO> generarAlertas(GenerarAlertasRequestDTO request) {
        log.info("🔍 Iniciando detección de productos de lento movimiento (>= {} días sin venta)", 
                 request.getDiasSinVentaMinimo());

        LocalDate fechaCorte = LocalDate.now().minusDays(request.getDiasSinVentaMinimo());
        List<Object[]> productosSinVentas = alertaRepository.detectarProductosSinVentas(fechaCorte);
        
        log.info("📊 Detectados {} productos candidatos", productosSinVentas.size());

        List<AlertaLentoMovimiento> alertasGeneradas = new ArrayList<>();

        for (Object[] row : productosSinVentas) {
            String skuInterno = (String) row[0];
            Integer sucursalId = ((Number) row[1]).intValue();
            Integer stockActual = ((Number) row[2]).intValue();
            BigDecimal cpp = (BigDecimal) row[3];
            Date ultimaVentaDate = (Date) row[4];
            LocalDate ultimaVenta = ultimaVentaDate != null ? ultimaVentaDate.toLocalDate() : null;
            Integer diasSinVenta = ((Number) row[5]).intValue();

            // Filtrar por sucursal si se especificó
            if (request.getSucursalId() != null && !request.getSucursalId().equals(sucursalId)) {
                continue;
            }

            // Verificar si ya existe alerta activa
            var alertaExistente = alertaRepository.findBySkuInternoAndSucursalIdAndResueltoFalse(
                    skuInterno, sucursalId);

            AlertaLentoMovimiento alerta;
            
            if (alertaExistente.isPresent()) {
                // ACTUALIZAR alerta existente
                alerta = alertaExistente.get();
                alerta.setDiasSinVenta(diasSinVenta);
                alerta.setStockActual(stockActual);
                alerta.calcularCostoInmovilizado(cpp);
                alerta.determinarEstadoAlerta();
                log.debug("♻️ Actualizando alerta existente: {} en sucursal {}", skuInterno, sucursalId);
            } else {
                if (request.getSoloActualizarExistentes()) {
                    continue; // Saltar creación de nuevas
                }
                
                // CREAR nueva alerta
                alerta = new AlertaLentoMovimiento();
                alerta.setSkuInterno(skuInterno);
                alerta.setSucursalId(sucursalId);
                alerta.setDiasSinVenta(diasSinVenta);
                alerta.setUltimaVenta(ultimaVenta);
                alerta.setStockActual(stockActual);
                alerta.calcularCostoInmovilizado(cpp);
                alerta.determinarEstadoAlerta();
                alerta.setFechaDeteccion(LocalDate.now());
                alerta.setResuelto(false);
                log.debug("🆕 Creando nueva alerta: {} en sucursal {}", skuInterno, sucursalId);
            }

            alertasGeneradas.add(alertaRepository.save(alerta));
        }

        log.info("✅ Generación completada: {} alertas procesadas", alertasGeneradas.size());
        
        return alertasGeneradas.stream()
                .map(this::convertirADTO)
                .toList();
    }

    /**
     * Obtiene todas las alertas activas del sistema.
     * 
     * @return Lista de alertas no resueltas
     */
    @Transactional(readOnly = true)
    public List<AlertaLentoMovimientoResponseDTO> obtenerAlertasActivas() {
        log.debug("Obteniendo todas las alertas activas");
        return alertaRepository.findAllActiveWithDetails().stream()
                .map(this::convertirADTO)
                .toList();
    }

    /**
     * Obtiene alertas activas de una sucursal específica.
     * 
     * @param sucursalId ID de la sucursal
     * @return Lista de alertas de la sucursal
     */
    @Transactional(readOnly = true)
    public List<AlertaLentoMovimientoResponseDTO> obtenerAlertasPorSucursal(Integer sucursalId) {
        log.debug("Obteniendo alertas activas para sucursal {}", sucursalId);
        return alertaRepository.findBySucursalIdAndResueltoFalse(sucursalId).stream()
                .map(this::convertirADTO)
                .toList();
    }

    /**
     * Obtiene solo las alertas en estado CRITICO (>60 días sin venta).
     * 
     * @return Lista de alertas críticas
     */
    @Transactional(readOnly = true)
    public List<AlertaLentoMovimientoResponseDTO> obtenerAlertasCriticas() {
        log.debug("Obteniendo alertas críticas");
        return alertaRepository.findCriticasActivas().stream()
                .map(this::convertirADTO)
                .toList();
    }

    /**
     * Obtiene alertas activas para un producto específico en todas las sucursales.
     * 
     * @param skuInterno SKU del producto
     * @return Lista de alertas del producto
     */
    @Transactional(readOnly = true)
    public List<AlertaLentoMovimientoResponseDTO> obtenerAlertasPorProducto(String skuInterno) {
        log.debug("Obteniendo alertas para producto {}", skuInterno);
        return alertaRepository.findBySkuInternoAndResueltoFalse(skuInterno).stream()
                .map(this::convertirADTO)
                .toList();
    }

    /**
     * Obtiene una alerta específica por ID.
     * 
     * @param id ID de la alerta
     * @return DTO con información de la alerta
     * @throws ResourceNotFoundException si la alerta no existe
     */
    @Transactional(readOnly = true)
    public AlertaLentoMovimientoResponseDTO obtenerAlertaPorId(Integer id) {
        AlertaLentoMovimiento alerta = alertaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Alerta no encontrada con ID: " + id));
        return convertirADTO(alerta);
    }

    /**
     * Calcula el resumen de costos inmovilizados por alertas.
     * 
     * @param sucursalId ID de sucursal (null para global)
     * @return DTO con métricas agregadas
     */
    @Transactional(readOnly = true)
    public CostoInmovilizadoResumenDTO calcularCostoInmovilizado(Integer sucursalId) {
        CostoInmovilizadoResumenDTO resumen = new CostoInmovilizadoResumenDTO();

        if (sucursalId != null) {
            Sucursal sucursal = sucursalRepository.findById(sucursalId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Sucursal no encontrada con ID: " + sucursalId));
            
            resumen.setSucursalId(sucursalId);
            resumen.setNombreSucursal(sucursal.getNombre());
            resumen.setCostoTotalInmovilizado(
                    alertaRepository.calcularCostoInmovilizadoPorSucursal(sucursalId));
        } else {
            resumen.setCostoTotalInmovilizado(
                    alertaRepository.calcularCostoTotalInmovilizado());
        }

        resumen.setAlertasAdvertencia(
                alertaRepository.countByEstadoAlertaAndResueltoFalse("ADVERTENCIA"));
        resumen.setAlertasCriticas(
                alertaRepository.countByEstadoAlertaAndResueltoFalse("CRITICO"));
        resumen.setTotalProductosAfectados(
                resumen.getAlertasAdvertencia() + resumen.getAlertasCriticas());

        return resumen;
    }

    /**
     * Marca una alerta como resuelta con la acción correctiva aplicada.
     * 
     * @param id ID de la alerta
     * @param request DTO con acción tomada y observaciones
     * @return DTO de la alerta resuelta
     * @throws ResourceNotFoundException si la alerta no existe
     */
    public AlertaLentoMovimientoResponseDTO resolverAlerta(Integer id, ResolverAlertaRequestDTO request) {
        log.info("✅ Resolviendo alerta {} con acción: {}", id, request.getAccionTomada());

        AlertaLentoMovimiento alerta = alertaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Alerta no encontrada con ID: " + id));

        alerta.resolver(request.getAccionTomada(), request.getObservaciones());
        AlertaLentoMovimiento alertaResuelta = alertaRepository.save(alerta);

        return convertirADTO(alertaResuelta);
    }

    /**
     * Elimina una alerta específica.
     * 
     * @param id ID de la alerta
     * @throws ResourceNotFoundException si la alerta no existe
     */
    public void eliminarAlerta(Integer id) {
        log.info("🗑️ Eliminando alerta {}", id);
        
        if (!alertaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Alerta no encontrada con ID: " + id);
        }
        
        alertaRepository.deleteById(id);
    }

    // ==========================================
    // MÉTODOS PRIVADOS DE CONVERSIÓN Y LÓGICA
    // ==========================================

    /**
     * Convierte una entidad AlertaLentoMovimiento a DTO.
     * 
     * @param alerta Entidad a convertir
     * @return DTO con información completa
     */
    private AlertaLentoMovimientoResponseDTO convertirADTO(AlertaLentoMovimiento alerta) {
        AlertaLentoMovimientoResponseDTO dto = new AlertaLentoMovimientoResponseDTO();
        
        dto.setId(alerta.getId());
        dto.setSkuInterno(alerta.getSkuInterno());
        dto.setSucursalId(alerta.getSucursalId());
        dto.setDiasSinVenta(alerta.getDiasSinVenta());
        dto.setUltimaVenta(alerta.getUltimaVenta());
        dto.setStockActual(alerta.getStockActual());
        dto.setCostoInmovilizado(alerta.getCostoInmovilizado());
        dto.setEstadoAlerta(alerta.getEstadoAlerta());
        dto.setFechaDeteccion(alerta.getFechaDeteccion());
        dto.setFechaResolucion(alerta.getFechaResolucion());
        dto.setAccionTomada(alerta.getAccionTomada());
        dto.setObservaciones(alerta.getObservaciones());
        dto.setResuelto(alerta.getResuelto());

        // Cargar información del producto si está disponible
        if (alerta.getProducto() != null) {
            dto.setNombreProducto(alerta.getProducto().getNombreComercial());
            dto.setMarcaProducto(alerta.getProducto().getMarca());
        }

        // Cargar información de la sucursal si está disponible
        if (alerta.getSucursal() != null) {
            dto.setNombreSucursal(alerta.getSucursal().getNombre());
        }

        // Calcular sugerencia de acción
        dto.setSugerenciaAccion(calcularSugerencia(alerta));

        return dto;
    }

    /**
     * Calcula sugerencia de acción correctiva basada en días sin venta y costo.
     * 
     * @param alerta Alerta a analizar
     * @return Sugerencia de acción
     */
    private String calcularSugerencia(AlertaLentoMovimiento alerta) {
        if (alerta.getDiasSinVenta() >= 90) {
            if (alerta.getCostoInmovilizado().compareTo(new BigDecimal("10000")) > 0) {
                return "LIQUIDACION URGENTE - Alto costo inmovilizado";
            }
            return "Considerar DESCONTINUAR o transferir a otra sucursal";
        } else if (alerta.getDiasSinVenta() >= 60) {
            return "Aplicar PROMOCION o descuento agresivo";
        } else if (alerta.getDiasSinVenta() >= 30) {
            return "Monitorear de cerca o aplicar promoción suave";
        }
        return "Revisar estrategia de precios";
    }
}
