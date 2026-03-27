package com.nexoohub.almacen.analitica.service;

import com.nexoohub.almacen.analitica.dto.RfmCalcularResponse;
import com.nexoohub.almacen.analitica.dto.RfmClienteResponse;
import com.nexoohub.almacen.analitica.dto.RfmSegmentoStatsResponse;
import com.nexoohub.almacen.analitica.entity.SegmentoRfmCliente;
import com.nexoohub.almacen.analitica.mapper.RfmMapper;
import com.nexoohub.almacen.analitica.repository.SegmentoRfmClienteRepository;
import com.nexoohub.almacen.common.exception.ResourceNotFoundException;
import com.nexoohub.almacen.ventas.entity.Venta;
import com.nexoohub.almacen.ventas.repository.VentaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RfmService {

    private static final Logger log = LoggerFactory.getLogger(RfmService.class);

    private final SegmentoRfmClienteRepository rfmRepository;
    private final VentaRepository ventaRepository;
    private final RfmMapper rfmMapper;

    public RfmService(SegmentoRfmClienteRepository rfmRepository, VentaRepository ventaRepository, RfmMapper rfmMapper) {
        this.rfmRepository = rfmRepository;
        this.ventaRepository = ventaRepository;
        this.rfmMapper = rfmMapper;
    }

    /**
     * Calcula y persiste el RFM de todos los clientes con al menos 1 venta concretada.
     */
    @Transactional
    public RfmCalcularResponse calcularRfmMasivo() {
        log.info("Iniciando cálculo masivo de RFM...");

        // 1. Obtener todas las ventas y filtrarlas (asumiendo que todas registradas son validas)
        List<Venta> todasLasVentas = ventaRepository.findAll();
        List<Venta> ventasConcretadas = todasLasVentas.stream()
                .filter(v -> v.getCliente() != null)
                .collect(Collectors.toList());

        LocalDateTime ahora = LocalDateTime.now();

        // Agrupación por Cliente ID
        Map<Integer, List<Venta>> ventasPorCliente = ventasConcretadas.stream()
                .collect(Collectors.groupingBy(v -> v.getCliente().getId()));

        if (ventasPorCliente.isEmpty()) {
            return new RfmCalcularResponse("No hay datos de ventas validos para evaluar RFM.", 0);
        }

        // 2. Extraer Valores en crudo (Raw Datos RFM por cliente)
        List<ClienteRfmData> clientesData = new ArrayList<>();
        for (Map.Entry<Integer, List<Venta>> entry : ventasPorCliente.entrySet()) {
            Integer clienteId = entry.getKey();
            List<Venta> comprasDelCliente = entry.getValue();

            // R: Días transcurridos desde última compra
            LocalDateTime ultimaCompra = comprasDelCliente.stream()
                    .map(Venta::getFechaVenta)
                    .max(LocalDateTime::compareTo)
                    .orElse(ahora);
            int recenciaDias = (int) ChronoUnit.DAYS.between(ultimaCompra, ahora);

            // F: Total de tickets/facturas distintas
            int frecuencia = comprasDelCliente.size();

            // M: Total gastado
            BigDecimal monto = comprasDelCliente.stream()
                    .map(v -> v.getTotal() != null ? v.getTotal() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            clientesData.add(new ClienteRfmData(clienteId, recenciaDias, frecuencia, monto, comprasDelCliente.get(0).getCliente()));
        }

        // 3. Asignación de Quintiles (Scores de 1 a 5)
        asignarScoresRFM(clientesData);

        // 4. Determinar los Segmentos Exactos y persistirlos
        int guardados = 0;
        for (ClienteRfmData data : clientesData) {
            String segmento = determinarSegmento(data.scoreR, data.scoreF, data.scoreM);

            SegmentoRfmCliente segmentoDb = rfmRepository.findByClienteId(data.clienteId)
                    .orElse(new SegmentoRfmCliente());

            segmentoDb.setClienteId(data.clienteId);
            segmentoDb.setCliente(data.cliente);
            segmentoDb.setRecenciaDias(data.recenciaDias);
            segmentoDb.setFrecuenciaCompras(data.frecuencia);
            segmentoDb.setMontoGastado(data.montoGastado);
            segmentoDb.setScoreR(data.scoreR);
            segmentoDb.setScoreF(data.scoreF);
            segmentoDb.setScoreM(data.scoreM);
            segmentoDb.setSegmento(segmento);
            segmentoDb.setFechaCalculo(ahora);

            rfmRepository.save(segmentoDb);
            guardados++;
        }

        log.info("Cálculo RFM finalizado con éxito. Se actualizaron {} clientes.", guardados);
        return new RfmCalcularResponse("Cálculo RFM masivo ejecutado correctamente.", guardados);
    }

    /**
     * Devuelve las estadísticas agrupadas de RFM.
     */
    @Transactional(readOnly = true)
    public List<RfmSegmentoStatsResponse> agruparClientesPorSegmento() {
        return rfmRepository.countBySegmento().stream()
                .map(stats -> new RfmSegmentoStatsResponse(stats.getSegmento(), stats.getCantidad()))
                .collect(Collectors.toList());
    }

    /**
     * Consulta estado de RFM para un cliente.
     */
    @Transactional(readOnly = true)
    public RfmClienteResponse obtenerRfmPorCliente(Integer clienteId) {
        SegmentoRfmCliente rfm = rfmRepository.findByClienteId(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("No existen métricas RFM calculadas para el cliente ID " + clienteId));
        return rfmMapper.toResponse(rfm);
    }


    /* -- MÉTODOS INTERNOS PARA EL ALGORITMO RFM -- */

    private void asignarScoresRFM(List<ClienteRfmData> clientes) {
        // Ordenamientos para percentiles:
        // Recency ascendente (menos días = mejor = Quintiles altos a los menores valores)
        clientes.sort(Comparator.comparingInt(c -> c.recenciaDias));
        asignarQuintilR(clientes);

        // Frecuencia ascendente (más compras = mejor = Quintiles altos a los mayores valores)
        clientes.sort(Comparator.comparingInt(c -> c.frecuencia));
        asignarQuintilFM(clientes, true);

        // Monetario ascendente (más monto = mejor = Quintiles altos a mayores valores)
        clientes.sort(Comparator.comparing(c -> c.montoGastado));
        asignarQuintilFM(clientes, false);
    }

    /**
     * En Recencia, los clientes recientes (menor número de días) sacan 5.
     */
    private void asignarQuintilR(List<ClienteRfmData> ordenadosRecencia) {
        int total = ordenadosRecencia.size();
        for (int i = 0; i < total; i++) {
            double percentil = (double) (i + 1) / total;
            if (percentil <= 0.20) ordenadosRecencia.get(i).scoreR = 5;
            else if (percentil <= 0.40) ordenadosRecencia.get(i).scoreR = 4;
            else if (percentil <= 0.60) ordenadosRecencia.get(i).scoreR = 3;
            else if (percentil <= 0.80) ordenadosRecencia.get(i).scoreR = 2;
            else ordenadosRecencia.get(i).scoreR = 1;
        }
    }

    /**
     * En F/M, los clientes con mayor valor sacan 5. Como están ordenados ascendente, los últimos sacan 5.
     */
    private void asignarQuintilFM(List<ClienteRfmData> ordenados, boolean isFrecuencia) {
        int total = ordenados.size();
        for (int i = 0; i < total; i++) {
            double percentil = (double) (i + 1) / total;
            int score;
            if (percentil <= 0.20) score = 1;
            else if (percentil <= 0.40) score = 2;
            else if (percentil <= 0.60) score = 3;
            else if (percentil <= 0.80) score = 4;
            else score = 5;

            if (isFrecuencia) {
                ordenados.get(i).scoreF = score;
            } else {
                ordenados.get(i).scoreM = score;
            }
        }
    }

    /**
     * Determina el segmento usando matriz RFM base de la industria (Scores 1 al 5).
     */
    private String determinarSegmento(int r, int f, int m) {
        // Un score RF promedio podría ser (R+F)/2, pero lo haremos con reglas explícitas de cruce
        
        // Campeones: Compraron recientemente, compran mucho frecuentemente, y gastan mucho.
        if (r >= 4 && f >= 4 && m >= 4) {
            return "CAMPEON";
        }
        // Leales: Compran regularmente, R puede estar ligeramente decaído pero F y M muy fuertes
        if (r >= 3 && f >= 4) {
            return "LEAL";
        }
        // Potencial (Nuevos o recientes que prometen): Alta recencia, baja frecuencia (primera o segunda compra) pero han hecho un esfuerzo.
        if (r >= 4 && f <= 3) {
            return "POTENCIAL_NUEVO";
        }
        // Necesitan Atención (EN_RIESGO): Solían ser buenos clientes pero la recencia decayó a mediano.
        if (r == 3 && f >= 2) {
            return "EN_RIESGO";
        }
        // En alerta de perder (No puedes perderlos): Fuerte frecuencia/monto histórico pero R es bajísima.
        if (r <= 2 && f >= 4) {
            return "ALERTA_CRITICO";
        }
        // Pasivos / Hibernando: Bajas frecuencias, media/baja recencia.
        if (r >= 2 && r <= 3 && f <= 3) {
            return "HIBERNANDO";
        }
        // Perdido: R=1, F=1-2, M=1-2.
        return "PERDIDO";
    }

    // POJO interno para ordenamiento sin tocar JPA Entity todavía
    private static class ClienteRfmData {
        Integer clienteId;
        int recenciaDias;
        int frecuencia;
        BigDecimal montoGastado;
        int scoreR, scoreF, scoreM;
        com.nexoohub.almacen.catalogo.entity.Cliente cliente;

        public ClienteRfmData(Integer clienteId, int recenciaDias, int frecuencia, BigDecimal montoGastado, com.nexoohub.almacen.catalogo.entity.Cliente cliente) {
            this.clienteId = clienteId;
            this.recenciaDias = recenciaDias;
            this.frecuencia = frecuencia;
            this.montoGastado = montoGastado;
            this.cliente = cliente;
        }
    }
}
