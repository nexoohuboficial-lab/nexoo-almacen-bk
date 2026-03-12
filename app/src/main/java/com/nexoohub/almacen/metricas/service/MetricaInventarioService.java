package com.nexoohub.almacen.metricas.service;

import com.nexoohub.almacen.inventario.entity.InventarioSucursal;
import com.nexoohub.almacen.inventario.repository.InventarioSucursalRepository;
import com.nexoohub.almacen.inventario.repository.ProductoMaestroRepository;
import com.nexoohub.almacen.metricas.dto.*;
import com.nexoohub.almacen.metricas.entity.MetricaInventario;
import com.nexoohub.almacen.metricas.repository.MetricaInventarioRepository;
import com.nexoohub.almacen.rentabilidad.repository.RentabilidadVentaRepository;
import com.nexoohub.almacen.sucursal.repository.SucursalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio para cálculo y gestión de métricas de inventario.
 * 
 * <p>Responde: <strong>¿Cuánto capital tengo inmovilizado? ¿Rota bien mi inventario?</strong></p>
 * 
 * <p>Métricas clave:</p>
 * <ul>
 *   <li>Valor total del inventario (capital inmovilizado)</li>
 *   <li>Rotación de inventario y días de inventario</li>
 *   <li>Stock disponible, bajo mínimo, sin stock</li>
 *   <li>Productos próximos a caducar</li>
 *   <li>Cobertura y eficiencia operativa</li>
 * </ul>
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
@Service
public class MetricaInventarioService {

    private static final Logger logger = LoggerFactory.getLogger(MetricaInventarioService.class);

    // Umbrales para clasificación de salud
    private static final BigDecimal ROTACION_ALTA = new BigDecimal("12"); // >=12 veces al año
    private static final BigDecimal ROTACION_MEDIA = new BigDecimal("6"); // >=6 veces al año
    private static final BigDecimal ROTACION_BAJA = new BigDecimal("3"); // >=3 veces al año

    private static final BigDecimal TASA_QUIEBRE_CRITICA = new BigDecimal("15.00"); // >=15% sin stock
    private static final BigDecimal TASA_QUIEBRE_ALTA = new BigDecimal("10.00"); // >=10% sin stock
    private static final BigDecimal TASA_QUIEBRE_MEDIA = new BigDecimal("5.00"); // >=5% sin stock

    private final MetricaInventarioRepository metricaInventarioRepository;
    private final InventarioSucursalRepository inventarioSucursalRepository;
    private final ProductoMaestroRepository productoMaestroRepository;
    private final RentabilidadVentaRepository rentabilidadVentaRepository;
    private final SucursalRepository sucursalRepository;

    public MetricaInventarioService(
            MetricaInventarioRepository metricaInventarioRepository,
            InventarioSucursalRepository inventarioSucursalRepository,
            ProductoMaestroRepository productoMaestroRepository,
            RentabilidadVentaRepository rentabilidadVentaRepository,
            SucursalRepository sucursalRepository) {
        this.metricaInventarioRepository = metricaInventarioRepository;
        this.inventarioSucursalRepository = inventarioSucursalRepository;
        this.productoMaestroRepository = productoMaestroRepository;
        this.rentabilidadVentaRepository = rentabilidadVentaRepository;
        this.sucursalRepository = sucursalRepository;
    }

    // ==================== MÉTODOS PÚBLICOS ====================

    /**
     * Genera análisis completo de inventario.
     * 
     * @param request Parámetros para el análisis
     * @return Métricas calculadas
     */
    @Transactional
    public MetricaInventarioResponseDTO generarAnalisisInventario(AnalisisInventarioRequestDTO request) {
        logger.info("Generando análisis de inventario - Fecha: {}, Sucursal: {}", 
                request.getFechaCorte(), request.getSucursalId());

        MetricaInventario metrica = calcularMetricas(
                request.getFechaCorte(),
                request.getSucursalId(),
                request.getDiasPeriodoRotacion()
        );

        if (request.getGuardarSnapshot()) {
            // Eliminar snapshot anterior si existe
            metricaInventarioRepository.deleteBySucursalIdAndFechaCorte(
                    request.getSucursalId(), request.getFechaCorte()
            );

            MetricaInventario guardada = metricaInventarioRepository.save(metrica);
            logger.info("Snapshot guardado con ID: {}", guardada.getId());
            return construirResponseDTO(guardada);
        } else {
            logger.info("Análisis calculado sin guardar snapshot");
            return construirResponseDTO(metrica);
        }
    }

    /**
     * Consulta métricas de inventario (snapshot o real-time).
     * 
     * @param fechaCorte Fecha del snapshot
     * @param sucursalId ID sucursal (null = consolidado)
     * @return Métricas de inventario
     */
    @Transactional(readOnly = true)
    public MetricaInventarioResponseDTO consultarMetricas(LocalDate fechaCorte, Integer sucursalId) {
        logger.info("Consultando métricas de inventario - Fecha: {}, Sucursal: {}", fechaCorte, sucursalId);

        Optional<MetricaInventario> metricaOpt;
        if (sucursalId == null) {
            metricaOpt = metricaInventarioRepository.findBySucursalIdIsNullAndFechaCorte(fechaCorte);
        } else {
            metricaOpt = metricaInventarioRepository.findBySucursalIdAndFechaCorte(sucursalId, fechaCorte);
        }

        if (metricaOpt.isPresent()) {
            logger.info("Snapshot encontrado ID: {}", metricaOpt.get().getId());
            return construirResponseDTO(metricaOpt.get());
        }

        logger.info("Snapshot no encontrado. Calculando en tiempo real...");
        MetricaInventario metrica = calcularMetricas(fechaCorte, sucursalId, 30);
        return construirResponseDTO(metrica);
    }

    /**
     * Obtiene productos con stock bajo mínimo.
     * 
     * @param sucursalId ID sucursal (null = todas)
     * @param limite Número máximo de resultados
     * @return Lista de productos bajo stock
     */
    @Transactional(readOnly = true)
    public List<ProductoInventarioDTO> obtenerProductosBajoStock(Integer sucursalId, int limite) {
        logger.info("Obteniendo productos bajo stock - Sucursal: {}, Limite: {}", sucursalId, limite);

        List<InventarioSucursal> inventarios;
        if (sucursalId == null) {
            inventarios = inventarioSucursalRepository.findAll();
        } else {
            inventarios = inventarioSucursalRepository.findByIdSucursalId(sucursalId);
        }

        return inventarios.stream()
                .filter(inv -> inv.getStockActual() > 0 && inv.getStockActual() < inv.getStockMinimoSucursal())
                .map(this::convertirAProductoDTO)
                .limit(limite)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene productos sin stock (quiebre).
     * 
     * @param sucursalId ID sucursal (null = todas)
     * @param limite Número máximo de resultados
     * @return Lista de productos sin stock
     */
    @Transactional(readOnly = true)
    public List<ProductoInventarioDTO> obtenerProductosSinStock(Integer sucursalId, int limite) {
        logger.info("Obteniendo productos sin stock - Sucursal: {}, Limite: {}", sucursalId, limite);

        List<InventarioSucursal> inventarios;
        if (sucursalId == null) {
            inventarios = inventarioSucursalRepository.findAll();
        } else {
            inventarios = inventarioSucursalRepository.findByIdSucursalId(sucursalId);
        }

        return inventarios.stream()
                .filter(inv -> inv.getStockActual() == 0)
                .map(this::convertirAProductoDTO)
                .limit(limite)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene productos próximos a caducar.
     * 
     * @param sucursalId ID sucursal (null = todas)
     * @param diasAnticipacion Días de anticipación (default: 30)
     * @param limite Número máximo de resultados
     * @return Lista de productos próximos a caducar
     */
    @Transactional(readOnly = true)
    public List<ProductoInventarioDTO> obtenerProductosProximosCaducar(
            Integer sucursalId, int diasAnticipacion, int limite) {
        logger.info("Obteniendo productos próximos a caducar - Sucursal: {}, Días: {}, Limite: {}", 
                sucursalId, diasAnticipacion, limite);

        LocalDate fechaLimite = LocalDate.now().plusDays(diasAnticipacion);

        List<InventarioSucursal> inventarios;
        if (sucursalId == null) {
            inventarios = inventarioSucursalRepository.findAll();
        } else {
            inventarios = inventarioSucursalRepository.findByIdSucursalId(sucursalId);
        }

        return inventarios.stream()
                .filter(inv -> inv.getFechaCaducidad() != null)
                .filter(inv -> inv.getFechaCaducidad().isBefore(fechaLimite) || inv.getFechaCaducidad().isEqual(fechaLimite))
                .map(this::convertirAProductoDTO)
                .limit(limite)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene histórico de métricas.
     * 
     * @param sucursalId ID sucursal (null = consolidado)
     * @param limite Número máximo de resultados
     * @return Lista de métricas históricas
     */
    @Transactional(readOnly = true)
    public List<MetricaInventarioResponseDTO> obtenerHistoricoMetricas(Integer sucursalId, int limite) {
        logger.info("Obteniendo histórico de métricas - Sucursal: {}, Limite: {}", sucursalId, limite);

        List<MetricaInventario> metricas;
        if (sucursalId == null) {
            metricas = metricaInventarioRepository.findBySucursalIdIsNullOrderByFechaCorteDesc();
        } else {
            metricas = metricaInventarioRepository.findBySucursalIdOrderByFechaCorteDesc(sucursalId);
        }

        return metricas.stream()
                .limit(limite)
                .map(this::construirResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene valor total del inventario actualizado.
     * 
     * @param sucursalId ID sucursal (null = consolidado)
     * @return Valor total en tiempo real
     */
    @Transactional(readOnly = true)
    public BigDecimal calcularValorInventarioActual(Integer sucursalId) {
        logger.info("Calculando valor actual del inventario - Sucursal: {}", sucursalId);

        List<InventarioSucursal> inventarios;
        if (sucursalId == null) {
            inventarios = inventarioSucursalRepository.findAll();
        } else {
            inventarios = inventarioSucursalRepository.findByIdSucursalId(sucursalId);
        }

        return inventarios.stream()
                .map(inv -> {
                    BigDecimal costo = inv.getCostoPromedioPonderado();
                    Integer stock = inv.getStockActual();
                    return costo.multiply(new BigDecimal(stock));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // ==================== MÉTODOS PRIVADOS ====================

    /**
     * Calcula todas las métricas de inventario.
     */
    private MetricaInventario calcularMetricas(LocalDate fechaCorte, Integer sucursalId, Integer diasPeriodo) {
        MetricaInventario metrica = new MetricaInventario();
        metrica.setFechaCorte(fechaCorte);
        metrica.setSucursalId(sucursalId);
        metrica.setDiasPeriodoRotacion(diasPeriodo);

        // Obtener inventarios
        List<InventarioSucursal> inventarios;
        if (sucursalId == null) {
            inventarios = inventarioSucursalRepository.findAll();
            metrica.setNombreSucursal("CONSOLIDADO");
        } else {
            inventarios = inventarioSucursalRepository.findByIdSucursalId(sucursalId);
            sucursalRepository.findById(sucursalId).ifPresent(suc -> metrica.setNombreSucursal(suc.getNombre()));
        }

        // 1. Métricas de Stock
        metrica.setTotalSkus(inventarios.size());
        metrica.setStockDisponibleTotal(
                inventarios.stream().mapToInt(InventarioSucursal::getStockActual).sum()
        );

        metrica.setSkusBajoStock((int) inventarios.stream()
                .filter(inv -> inv.getStockActual() < inv.getStockMinimoSucursal())
                .count());

        metrica.setSkusSinStock((int) inventarios.stream()
                .filter(inv -> inv.getStockActual() == 0)
                .count());

        LocalDate fechaLimiteCaducidad = fechaCorte.plusDays(30);
        metrica.setSkusProximosCaducar((int) inventarios.stream()
                .filter(inv -> inv.getFechaCaducidad() != null)
                .filter(inv -> inv.getFechaCaducidad().isBefore(fechaLimiteCaducidad)
                        || inv.getFechaCaducidad().isEqual(fechaLimiteCaducidad))
                .count());

        // 2. Métricas de Valor
        BigDecimal valorTotal = inventarios.stream()
                .map(inv -> inv.getCostoPromedioPonderado().multiply(new BigDecimal(inv.getStockActual())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        metrica.setValorTotalInventario(valorTotal);

        if (metrica.getStockDisponibleTotal() > 0) {
            metrica.setCostoPromedioPonderado(
                    valorTotal.divide(new BigDecimal(metrica.getStockDisponibleTotal()), 2, RoundingMode.HALF_UP)
            );
        }

        BigDecimal valorStockBajo = inventarios.stream()
                .filter(inv -> inv.getStockActual() < inv.getStockMinimoSucursal())
                .map(inv -> inv.getCostoPromedioPonderado().multiply(new BigDecimal(inv.getStockActual())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        metrica.setValorStockBajo(valorStockBajo);

        // 3. Métricas de Rotación (basadas en costo de ventas del período)
        LocalDate fechaInicioPeriodo = fechaCorte.minusDays(diasPeriodo);
        BigDecimal costoVentas = rentabilidadVentaRepository.calcularCostoTotalPeriodo(
                fechaInicioPeriodo, fechaCorte
        );
        metrica.setCostoVentasPeriodo(costoVentas != null ? costoVentas : BigDecimal.ZERO);

        if (valorTotal.compareTo(BigDecimal.ZERO) > 0 && metrica.getCostoVentasPeriodo().compareTo(BigDecimal.ZERO) > 0) {
            // Rotación = Costo Ventas / Valor Inventario (anualizado)
            BigDecimal rotacionPeriodo = metrica.getCostoVentasPeriodo().divide(valorTotal, 4, RoundingMode.HALF_UP);
            BigDecimal factorAnualizacion = new BigDecimal("365").divide(new BigDecimal(diasPeriodo), 4, RoundingMode.HALF_UP);
            BigDecimal rotacionAnual = rotacionPeriodo.multiply(factorAnualizacion);
            metrica.setIndiceRotacion(rotacionAnual);

            // Días de inventario = 365 / Rotación
            metrica.setDiasInventario(
                    new BigDecimal("365").divide(rotacionAnual, 2, RoundingMode.HALF_UP)
            );
        } else {
            metrica.setIndiceRotacion(BigDecimal.ZERO);
            metrica.setDiasInventario(BigDecimal.ZERO);
        }

        // 4. Métricas de Eficiencia
        if (metrica.getTotalSkus() > 0) {
            BigDecimal tasaQuiebre = new BigDecimal(metrica.getSkusSinStock())
                    .divide(new BigDecimal(metrica.getTotalSkus()), 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
            metrica.setTasaQuiebreStock(tasaQuiebre);
        }

        // Cobertura (días de stock disponible)
        if (metrica.getCostoVentasPeriodo().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal ventaDiaria = metrica.getCostoVentasPeriodo().divide(
                    new BigDecimal(diasPeriodo), 2, RoundingMode.HALF_UP
            );
            metrica.setCoberturaDias(
                    valorTotal.divide(ventaDiaria, 2, RoundingMode.HALF_UP)
            );
        }

        // 5. Clasificaciones
        metrica.setClasificacionRotacion(clasificarRotacion(metrica.getIndiceRotacion()));
        metrica.setSaludInventario(evaluarSaludInventario(metrica));

        logger.info("Métricas calculadas - Valor: {}, SKUs: {}, Rotación: {}",
                metrica.getValorTotalInventario(), metrica.getTotalSkus(), metrica.getIndiceRotacion());

        return metrica;
    }

    /**
     * Construye DTO de respuesta con enriquecimiento.
     */
    private MetricaInventarioResponseDTO construirResponseDTO(MetricaInventario metrica) {
        MetricaInventarioResponseDTO dto = new MetricaInventarioResponseDTO();

        dto.setId(metrica.getId());
        dto.setFechaCorte(metrica.getFechaCorte());
        dto.setSucursalId(metrica.getSucursalId());
        dto.setNombreSucursal(metrica.getNombreSucursal());

        // Stock
        dto.setTotalSkus(metrica.getTotalSkus());
        dto.setStockDisponibleTotal(metrica.getStockDisponibleTotal());
        dto.setSkusBajoStock(metrica.getSkusBajoStock());
        dto.setSkusSinStock(metrica.getSkusSinStock());
        dto.setSkusProximosCaducar(metrica.getSkusProximosCaducar());

        // Valor
        dto.setValorTotalInventario(metrica.getValorTotalInventario());
        dto.setCostoPromedioPonderado(metrica.getCostoPromedioPonderado());
        dto.setValorStockBajo(metrica.getValorStockBajo());

        // Rotación
        dto.setIndiceRotacion(metrica.getIndiceRotacion());
        dto.setDiasInventario(metrica.getDiasInventario());
        dto.setCostoVentasPeriodo(metrica.getCostoVentasPeriodo());
        dto.setDiasPeriodoRotacion(metrica.getDiasPeriodoRotacion());

        // Eficiencia
        dto.setCoberturaDias(metrica.getCoberturaDias());
        dto.setExactitudPorcentaje(metrica.getExactitudPorcentaje());
        dto.setTasaQuiebreStock(metrica.getTasaQuiebreStock());

        // Clasificaciones
        dto.setSaludInventario(metrica.getSaludInventario());
        dto.setClasificacionRotacion(metrica.getClasificacionRotacion());

        // Cálculos derivados
        dto.setSkusConStock(metrica.getTotalSkus() - metrica.getSkusSinStock());

        if (metrica.getTotalSkus() > 0) {
            dto.setPorcentajeBajoStock(
                    new BigDecimal(metrica.getSkusBajoStock())
                            .divide(new BigDecimal(metrica.getTotalSkus()), 4, RoundingMode.HALF_UP)
                            .multiply(new BigDecimal("100"))
            );
            dto.setPorcentajeSinStock(
                    new BigDecimal(metrica.getSkusSinStock())
                            .divide(new BigDecimal(metrica.getTotalSkus()), 4, RoundingMode.HALF_UP)
                            .multiply(new BigDecimal("100"))
            );
            dto.setPorcentajeProximosCaducar(
                    new BigDecimal(metrica.getSkusProximosCaducar())
                            .divide(new BigDecimal(metrica.getTotalSkus()), 4, RoundingMode.HALF_UP)
                            .multiply(new BigDecimal("100"))
            );
        }

        // Auditoría
        dto.setCreatedAt(metrica.getFechaCreacion());
        dto.setCreatedBy(metrica.getUsuarioCreacion());

        return dto;
    }

    /**
     * Convierte InventarioSucursal a ProductoInventarioDTO.
     */
    private ProductoInventarioDTO convertirAProductoDTO(InventarioSucursal inventario) {
        ProductoInventarioDTO dto = new ProductoInventarioDTO();
        dto.setSkuInterno(inventario.getId().getSkuInterno());
        dto.setStockActual(inventario.getStockActual());
        dto.setStockMinimo(inventario.getStockMinimoSucursal());
        dto.setCostoPromedioPonderado(inventario.getCostoPromedioPonderado());
        dto.setValorInventario(
                inventario.getCostoPromedioPonderado().multiply(new BigDecimal(inventario.getStockActual()))
        );
        dto.setFechaCaducidad(inventario.getFechaCaducidad());
        dto.setUbicacionPasillo(inventario.getUbicacionPasillo());
        dto.setSucursalId(inventario.getId().getSucursalId());

        // Enriquecer con datos del producto
        productoMaestroRepository.findById(inventario.getId().getSkuInterno()).ifPresent(producto -> {
            dto.setNombreComercial(producto.getNombreComercial());
            dto.setMarca(producto.getMarca());
            if (producto.getCategoria() != null) {
                dto.setCategoria(producto.getCategoria().getNombre());
            }
        });

        // Enriquecer con nombre de sucursal
        sucursalRepository.findById(inventario.getId().getSucursalId()).ifPresent(sucursal -> {
            dto.setNombreSucursal(sucursal.getNombre());
        });

        // Estado de alerta
        if (inventario.getStockActual() == 0) {
            dto.setEstadoAlerta("SIN_STOCK");
        } else if (inventario.getStockActual() < inventario.getStockMinimoSucursal()) {
            dto.setEstadoAlerta("BAJO_STOCK");
        } else if (inventario.getFechaCaducidad() != null 
                && inventario.getFechaCaducidad().isBefore(LocalDate.now().plusDays(30))) {
            dto.setEstadoAlerta("PROXIMO_CADUCAR");
        } else {
            dto.setEstadoAlerta("OK");
        }

        return dto;
    }

    /**
     * Clasifica la rotación de inventario.
     */
    private String clasificarRotacion(BigDecimal rotacion) {
        if (rotacion.compareTo(ROTACION_ALTA) >= 0) {
            return "ALTA"; // >=12 veces al año = mensual
        } else if (rotacion.compareTo(ROTACION_MEDIA) >= 0) {
            return "MEDIA"; // >=6 veces al año = bimestral
        } else if (rotacion.compareTo(ROTACION_BAJA) >= 0) {
            return "BAJA"; // >=3 veces al año = trimestral
        } else {
            return "MUY_BAJA"; // <3 veces al año
        }
    }

    /**
     * Evalúa salud general del inventario.
     */
    private String evaluarSaludInventario(MetricaInventario metrica) {
        BigDecimal rotacion = metrica.getIndiceRotacion();
        BigDecimal tasaQuiebre = metrica.getTasaQuiebreStock();
        int skusSinStock = metrica.getSkusSinStock();

        // CRITICA: Alta tasa de quiebre o rotación muy baja
        if (tasaQuiebre.compareTo(TASA_QUIEBRE_CRITICA) >= 0 || rotacion.compareTo(ROTACION_BAJA) < 0) {
            return "CRITICA";
        }

        // REQUIERE_ATENCION: Quiebre medio o rotación baja con problemas de stock
        if (tasaQuiebre.compareTo(TASA_QUIEBRE_ALTA) >= 0 
                || (rotacion.compareTo(ROTACION_MEDIA) < 0 && skusSinStock > 0)) {
            return "REQUIERE_ATENCION";
        }

        // ACEPTABLE: Rotación media, pocos quiebres
        if (rotacion.compareTo(ROTACION_MEDIA) >= 0 && tasaQuiebre.compareTo(TASA_QUIEBRE_MEDIA) < 0) {
            return "ACEPTABLE";
        }

        // SALUDABLE: Rotación alta, bajos quiebres
        if (rotacion.compareTo(ROTACION_ALTA) >= 0 && tasaQuiebre.compareTo(TASA_QUIEBRE_MEDIA) < 0) {
            return "SALUDABLE";
        }

        return "ACEPTABLE";
    }
}
