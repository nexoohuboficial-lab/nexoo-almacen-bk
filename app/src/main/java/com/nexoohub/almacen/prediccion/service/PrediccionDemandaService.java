package com.nexoohub.almacen.prediccion.service;

import com.nexoohub.almacen.inventario.entity.InventarioSucursal;
import com.nexoohub.almacen.inventario.entity.ProductoMaestro;
import com.nexoohub.almacen.inventario.repository.InventarioSucursalRepository;
import com.nexoohub.almacen.inventario.repository.ProductoMaestroRepository;
import com.nexoohub.almacen.prediccion.dto.GenerarPrediccionRequestDTO;
import com.nexoohub.almacen.prediccion.dto.PrediccionDemandaResponseDTO;
import com.nexoohub.almacen.prediccion.dto.RecomendacionCompraDTO;
import com.nexoohub.almacen.prediccion.entity.PrediccionDemanda;
import com.nexoohub.almacen.prediccion.mapper.PrediccionDemandaMapper;
import com.nexoohub.almacen.prediccion.repository.PrediccionDemandaRepository;
import com.nexoohub.almacen.sucursal.entity.Sucursal;
import com.nexoohub.almacen.sucursal.repository.SucursalRepository;
import com.nexoohub.almacen.ventas.entity.DetalleVenta;
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
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service para gestionar predicciones de demanda.
 * 
 * <p>Analiza histórico de ventas y genera proyecciones para optimizar compras.</p>
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
@Service
public class PrediccionDemandaService {

    private final PrediccionDemandaRepository prediccionRepository;
    private final VentaRepository ventaRepository;
    private final InventarioSucursalRepository inventarioRepository;
    private final ProductoMaestroRepository productoRepository;
    private final SucursalRepository sucursalRepository;
    private final PrediccionDemandaMapper mapper;

    public PrediccionDemandaService(
            PrediccionDemandaRepository prediccionRepository,
            VentaRepository ventaRepository,
            InventarioSucursalRepository inventarioRepository,
            ProductoMaestroRepository productoRepository,
            SucursalRepository sucursalRepository,
            PrediccionDemandaMapper mapper) {
        this.prediccionRepository = prediccionRepository;
        this.ventaRepository = ventaRepository;
        this.inventarioRepository = inventarioRepository;
        this.productoRepository = productoRepository;
        this.sucursalRepository = sucursalRepository;
        this.mapper = mapper;
    }

    // ==========================================
    // GENERACIÓN DE PREDICCIONES
    // ==========================================

    /**
     * Genera predicciones de demanda para un periodo
     */
    @Transactional
    public List<PrediccionDemandaResponseDTO> generarPredicciones(GenerarPrediccionRequestDTO request) {
        // Validar sucursal
        Sucursal sucursal = sucursalRepository.findById(request.getSucursalId())
                .orElseThrow(() -> new EntityNotFoundException("Sucursal no encontrada"));

        List<PrediccionDemanda> predicciones = new ArrayList<>();

        if (request.getSkuProducto() != null) {
            // Predicción para un producto específico
            PrediccionDemanda prediccion = generarPrediccionProducto(
                    request.getSkuProducto(),
                    request.getSucursalId(),
                    request.getPeriodoAnio(),
                    request.getPeriodoMes(),
                    request.getMesesHistoricos(),
                    request.getDiasStockSeguridad(),
                    request.getMetodoCalculo()
            );
            if (prediccion != null) {
                predicciones.add(prediccion);
            }
        } else {
            // Predicciones para todos los productos con inventario
            List<InventarioSucursal> inventarios = inventarioRepository
                    .findByIdSucursalId(request.getSucursalId());

            for (InventarioSucursal inv : inventarios) {
                try {
                    PrediccionDemanda prediccion = generarPrediccionProducto(
                            inv.getId().getSkuInterno(),
                            request.getSucursalId(),
                            request.getPeriodoAnio(),
                            request.getPeriodoMes(),
                            request.getMesesHistoricos(),
                            request.getDiasStockSeguridad(),
                            request.getMetodoCalculo()
                    );
                    if (prediccion != null) {
                        predicciones.add(prediccion);
                    }
                } catch (Exception e) {
                    // Continuar con otros productos si uno falla
                    continue;
                }
            }
        }

        // Guardar predicciones
        List<PrediccionDemanda> guardadas = prediccionRepository.saveAll(predicciones);

        // Convertir a DTOs
        return guardadas.stream()
                .map(p -> {
                    ProductoMaestro producto = productoRepository
                            .findById(p.getSkuProducto()).orElse(null);
                    return mapper.toDTO(p, producto, sucursal);
                })
                .collect(Collectors.toList());
    }

    /**
     * Genera predicción para un producto específico
     */
    private PrediccionDemanda generarPrediccionProducto(
            String skuProducto,
            Integer sucursalId,
            Integer periodoAnio,
            Integer periodoMes,
            Integer mesesHistoricos,
            Integer diasStockSeguridad,
            String metodoCalculo) {

        // Verificar si ya existe predicción
        Optional<PrediccionDemanda> existente = prediccionRepository
                .findBySkuProductoAndSucursalIdAndPeriodoAnioAndPeriodoMes(
                        skuProducto, sucursalId, periodoAnio, periodoMes);

        PrediccionDemanda prediccion = existente.orElse(new PrediccionDemanda());
        prediccion.setSkuProducto(skuProducto);
        prediccion.setSucursalId(sucursalId);
        prediccion.setPeriodoAnio(periodoAnio);
        prediccion.setPeriodoMes(periodoMes);

        // Obtener histórico de ventas
        List<VentaHistorica> historico = obtenerHistoricoVentas(
                skuProducto, sucursalId, periodoAnio, periodoMes, mesesHistoricos);

        if (historico.isEmpty()) {
            // No hay histórico, no se puede predecir
            return null;
        }

        // Calcular demanda histórica promedio
        BigDecimal demandaHistorica = calcularPromedioVentas(historico);
        prediccion.setDemandaHistorica(demandaHistorica);

        // Calcular tendencia
        BigDecimal tendencia = calcularTendencia(historico);
        prediccion.setTendencia(tendencia);

        // Calcular demanda predicha según método
        BigDecimal demandaPredicha;
        switch (metodoCalculo.toUpperCase()) {
            case "TENDENCIA_LINEAL":
                demandaPredicha = calcularDemandaTendenciaLineal(demandaHistorica, tendencia);
                break;
            case "ESTACIONAL":
                demandaPredicha = calcularDemandaEstacional(historico, periodoMes);
                break;
            case "PROMEDIO_MOVIL":
            default:
                demandaPredicha = calcularDemandaPromedioMovil(historico, 3);
                break;
        }
        prediccion.setDemandaPredicha(demandaPredicha);

        // Obtener stock actual
        Integer stockActual = inventarioRepository
                .findByIdSkuInternoAndIdSucursalId(skuProducto, sucursalId)
                .map(InventarioSucursal::getStockActual)
                .orElse(0);
        prediccion.setStockActual(stockActual);

        // Calcular stock de seguridad (demanda diaria * días)
        int diasDelMes = YearMonth.of(periodoAnio, periodoMes).lengthOfMonth();
        BigDecimal demandaDiaria = demandaPredicha.divide(
                BigDecimal.valueOf(diasDelMes), 2, RoundingMode.HALF_UP);
        int stockSeguridad = demandaDiaria.multiply(BigDecimal.valueOf(diasStockSeguridad))
                .setScale(0, RoundingMode.CEILING).intValue();
        prediccion.setStockSeguridad(stockSeguridad);

        // Calcular stock sugerido (demanda predicha + stock seguridad)
        int stockSugerido = demandaPredicha.setScale(0, RoundingMode.CEILING).intValue() 
                + stockSeguridad;
        prediccion.setStockSugerido(stockSugerido);

        // Calcular cantidad a comprar
        int cantidadComprar = Math.max(0, stockSugerido - stockActual);
        prediccion.setCantidadComprar(cantidadComprar);

        // Calcular nivel de confianza
        BigDecimal nivelConfianza = calcularNivelConfianza(historico);
        prediccion.setNivelConfianza(nivelConfianza);

        // Metadata
        prediccion.setMetodoCalculo(metodoCalculo);
        prediccion.setPeriodosAnalizados(historico.size());
        prediccion.setFechaCalculo(LocalDate.now());

        // Observaciones
        String obs = generarObservaciones(historico, demandaPredicha, tendencia);
        prediccion.setObservaciones(obs);

        return prediccion;
    }

    // ==========================================
    // ANÁLISIS HISTÓRICO
    // ==========================================

    /**
     * Obtiene histórico de ventas por producto y periodo
     */
    private List<VentaHistorica> obtenerHistoricoVentas(
            String skuProducto,
            Integer sucursalId,
            Integer periodoAnio,
            Integer periodoMes,
            Integer mesesHistoricos) {

        List<VentaHistorica> historico = new ArrayList<>();
        YearMonth periodoPrediccion = YearMonth.of(periodoAnio, periodoMes);

        // Iterar hacia atrás en el tiempo
        for (int i = 1; i <= mesesHistoricos; i++) {
            YearMonth periodo = periodoPrediccion.minusMonths(i);
            LocalDateTime inicio = periodo.atDay(1).atStartOfDay();
            LocalDateTime fin = periodo.atEndOfMonth().atTime(23, 59, 59);

            // Obtener ventas del periodo
            List<Venta> ventas = ventaRepository.findByFechaRangoConDetalles(inicio, fin);

            // Sumar cantidad vendida del producto en esta sucursal
            int cantidadVendida = 0;
            for (Venta venta : ventas) {
                if (venta.getSucursalId() != null && venta.getSucursalId().equals(sucursalId)) {
                    for (DetalleVenta detalle : venta.getDetalles()) {
                        if (detalle.getSkuInterno() != null && 
                            detalle.getSkuInterno().equals(skuProducto)) {
                            cantidadVendida += detalle.getCantidad();
                        }
                    }
                }
            }

            historico.add(new VentaHistorica(periodo, cantidadVendida));
        }

        // Ordenar del más antiguo al más reciente
        Collections.reverse(historico);
        return historico;
    }

    /**
     * Calcula promedio de ventas
     */
    private BigDecimal calcularPromedioVentas(List<VentaHistorica> historico) {
        if (historico.isEmpty()) {
            return BigDecimal.ZERO;
        }

        double suma = historico.stream()
                .mapToInt(VentaHistorica::getCantidad)
                .sum();

        return BigDecimal.valueOf(suma / historico.size())
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calcula tendencia lineal (pendiente)
     */
    private BigDecimal calcularTendencia(List<VentaHistorica> historico) {
        if (historico.size() < 2) {
            return BigDecimal.ZERO;
        }

        int n = historico.size();
        double sumaX = 0, sumaY = 0, sumaXY = 0, sumaX2 = 0;

        for (int i = 0; i < n; i++) {
            double x = i + 1; // Periodo (1, 2, 3, ...)
            double y = historico.get(i).getCantidad();

            sumaX += x;
            sumaY += y;
            sumaXY += x * y;
            sumaX2 += x * x;
        }

        // Pendiente: (n*ΣXY - ΣX*ΣY) / (n*ΣX² - (ΣX)²)
        double numerador = n * sumaXY - sumaX * sumaY;
        double denominador = n * sumaX2 - sumaX * sumaX;

        if (denominador == 0) {
            return BigDecimal.ZERO;
        }

        double pendiente = numerador / denominador;
        return BigDecimal.valueOf(pendiente).setScale(4, RoundingMode.HALF_UP);
    }

    /**
     * Calcula demanda con tendencia lineal
     */
    private BigDecimal calcularDemandaTendenciaLineal(BigDecimal promedio, BigDecimal tendencia) {
        // Predicción = promedio + tendencia
        return promedio.add(tendencia).max(BigDecimal.ZERO);
    }

    /**
     * Calcula demanda con promedio móvil
     */
    private BigDecimal calcularDemandaPromedioMovil(List<VentaHistorica> historico, int periodos) {
        if (historico.size() < periodos) {
            return calcularPromedioVentas(historico);
        }

        // Tomar últimos N periodos
        List<VentaHistorica> ultimos = historico.subList(
                historico.size() - periodos, historico.size());

        return calcularPromedioVentas(ultimos);
    }

    /**
     * Calcula demanda considerando estacionalidad
     */
    private BigDecimal calcularDemandaEstacional(List<VentaHistorica> historico, int mesPredecir) {
        // Buscar ventas del mismo mes en años anteriores
        List<Integer> ventasMismoMes = historico.stream()
                .filter(v -> v.getPeriodo().getMonthValue() == mesPredecir)
                .map(VentaHistorica::getCantidad)
                .collect(Collectors.toList());

        if (ventasMismoMes.isEmpty()) {
            return calcularPromedioVentas(historico);
        }

        double promedioMes = ventasMismoMes.stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);

        return BigDecimal.valueOf(promedioMes).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calcula nivel de confianza basado en variabilidad
     */
    private BigDecimal calcularNivelConfianza(List<VentaHistorica> historico) {
        if (historico.size() < 2) {
            return BigDecimal.valueOf(50); // Confianza baja
        }

        // Calcular desviación estándar
        double promedio = historico.stream()
                .mapToInt(VentaHistorica::getCantidad)
                .average()
                .orElse(0.0);

        double sumaCuadrados = historico.stream()
                .mapToDouble(v -> Math.pow(v.getCantidad() - promedio, 2))
                .sum();

        double varianza = sumaCuadrados / historico.size();
        double desviacion = Math.sqrt(varianza);

        // Coeficiente de variación
        double coeficienteVar = promedio > 0 ? (desviacion / promedio) : 0;

        // Nivel de confianza: 100% cuando CV=0, decrece con mayor variabilidad
        double confianza = Math.max(0, 100 - (coeficienteVar * 100));

        return BigDecimal.valueOf(confianza).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Genera observaciones automáticas
     */
    private String generarObservaciones(List<VentaHistorica> historico, 
                                       BigDecimal demandaPredicha, 
                                       BigDecimal tendencia) {
        StringBuilder obs = new StringBuilder();

        if (tendencia.compareTo(BigDecimal.ZERO) > 0) {
            obs.append("Tendencia creciente. ");
        } else if (tendencia.compareTo(BigDecimal.ZERO) < 0) {
            obs.append("Tendencia decreciente. ");
        }

        if (historico.size() < 3) {
            obs.append("Datos históricos limitados. ");
        }

        return obs.toString().trim();
    }

    // ==========================================
    // CONSULTAS
    // ==========================================

    /**
     * Obtiene predicción por ID
     */
    @Transactional(readOnly = true)
    public PrediccionDemandaResponseDTO obtenerPrediccion(Integer id) {
        PrediccionDemanda prediccion = prediccionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Predicción no encontrada"));

        ProductoMaestro producto = productoRepository
                .findById(prediccion.getSkuProducto()).orElse(null);
        Sucursal sucursal = sucursalRepository
                .findById(prediccion.getSucursalId()).orElse(null);

        return mapper.toDTO(prediccion, producto, sucursal);
    }

    /**
     * Obtiene predicciones de un producto en una sucursal
     */
    @Transactional(readOnly = true)
    public List<PrediccionDemandaResponseDTO> obtenerPrediccionesProducto(
            String skuProducto, Integer sucursalId) {

        List<PrediccionDemanda> predicciones = prediccionRepository
                .findBySkuProductoAndSucursalIdOrderByPeriodoAnioDescPeriodoMesDesc(
                        skuProducto, sucursalId);

        ProductoMaestro producto = productoRepository.findById(skuProducto).orElse(null);
        Sucursal sucursal = sucursalRepository.findById(sucursalId).orElse(null);

        return predicciones.stream()
                .map(p -> mapper.toDTO(p, producto, sucursal))
                .collect(Collectors.toList());
    }

    /**
     * Obtiene recomendaciones de compra para un periodo
     */
    @Transactional(readOnly = true)
    public RecomendacionCompraDTO obtenerRecomendacionesCompra(
            Integer sucursalId, Integer periodoAnio, Integer periodoMes) {

        Sucursal sucursal = sucursalRepository.findById(sucursalId)
                .orElseThrow(() -> new EntityNotFoundException("Sucursal no encontrada"));

        List<PrediccionDemanda> productosComprar = prediccionRepository
                .findProductosParaComprar(sucursalId, periodoAnio, periodoMes);

        List<PrediccionDemandaResponseDTO> dtos = productosComprar.stream()
                .map(p -> {
                    ProductoMaestro producto = productoRepository
                            .findById(p.getSkuProducto()).orElse(null);
                    return mapper.toDTO(p, producto, sucursal);
                })
                .collect(Collectors.toList());

        // Calcular totales
        int unidadesTotales = productosComprar.stream()
                .mapToInt(PrediccionDemanda::getCantidadComprar)
                .sum();

        Month month = Month.of(periodoMes);
        String nombreMes = month.getDisplayName(TextStyle.FULL, Locale.of("es", "MX"));
        String periodoTexto = nombreMes.substring(0, 1).toUpperCase() + 
                             nombreMes.substring(1) + " " + periodoAnio;

        long totalPredicciones = prediccionRepository
                .countBySucursalIdAndPeriodoAnioAndPeriodoMes(sucursalId, periodoAnio, periodoMes);

        return new RecomendacionCompraDTO(
                sucursalId,
                sucursal.getNombre(),
                periodoAnio,
                periodoMes,
                periodoTexto,
                (int) totalPredicciones,
                productosComprar.size(),
                unidadesTotales,
                BigDecimal.ZERO, // Costo estimado (requiere precios)
                dtos
        );
    }

    // ==========================================
    // CLASE INTERNA PARA HISTÓRICO
    // ==========================================

    private static class VentaHistorica {
        private final YearMonth periodo;
        private final int cantidad;

        public VentaHistorica(YearMonth periodo, int cantidad) {
            this.periodo = periodo;
            this.cantidad = cantidad;
        }

        public YearMonth getPeriodo() {
            return periodo;
        }

        public int getCantidad() {
            return cantidad;
        }
    }
}
