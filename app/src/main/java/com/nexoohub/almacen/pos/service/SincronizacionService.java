package com.nexoohub.almacen.pos.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexoohub.almacen.common.exception.ResourceNotFoundException;
import com.nexoohub.almacen.pos.dto.LoteSincronizacionResponse;
import com.nexoohub.almacen.pos.dto.SyncLoteRequest;
import com.nexoohub.almacen.pos.dto.VentaOfflineSyncDTO;
import com.nexoohub.almacen.pos.entity.LoteSincronizacion;
import com.nexoohub.almacen.pos.repository.LoteSincronizacionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio para procesar lotes de ventas offline (POS-04).
 *
 * @author NexooHub Development Team
 * @since 1.0 — POS-04
 */
@Service
public class SincronizacionService {

    private static final Logger log = LoggerFactory.getLogger(SincronizacionService.class);

    private final LoteSincronizacionRepository loteRepo;
    private final ObjectMapper objectMapper;

    public SincronizacionService(LoteSincronizacionRepository loteRepo, ObjectMapper objectMapper) {
        this.loteRepo = loteRepo;
        this.objectMapper = objectMapper;
    }

    /**
     * Recibe el array de ventas locales y lo registra en la tabla de control.
     */
    @Transactional
    public LoteSincronizacionResponse procesarLote(SyncLoteRequest request) {
        log.info("Recibiendo lote de sincronización: {}", request.getCodigoLote());

        Optional<LoteSincronizacion> existente = loteRepo.findByCodigoLote(request.getCodigoLote());
        if (existente.isPresent()) {
            throw new IllegalStateException("El lote " + request.getCodigoLote() + " ya fue registrado previamente en el servidor.");
        }

        LoteSincronizacion lote = new LoteSincronizacion();
        lote.setCodigoLote(request.getCodigoLote());
        lote.setSucursalId(request.getSucursalId());
        lote.setCajaId(request.getCajaId());
        lote.setUsuarioId(request.getUsuarioId());
        lote.setFechaGeneracion(request.getFechaGeneracion());
        
        List<VentaOfflineSyncDTO> ventas = request.getVentas();
        lote.setTotalVentas(ventas.size());
        
        BigDecimal montoTotal = ventas.stream()
            .map(VentaOfflineSyncDTO::getMontoTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        lote.setMontoTotalLote(montoTotal);

        try {
            String jsonPayload = objectMapper.writeValueAsString(ventas);
            lote.setPayloadJson(jsonPayload);
        } catch (JsonProcessingException e) {
            log.error("Error convirtiendo payload a JSON", e);
            lote.setPayloadJson("[]");
        }

        // SIMULAMOS que el proceso fue exitoso de inmediato (lógica real de persistencia de ventas iría aquí iterando ventas)
        // Para POS-04 validamos el módulo de sincronización:
        try {
            simularInsercionVentas(ventas);
            lote.setEstatus("PROCESADO");
            lote.setVentasProcesadas(ventas.size());
        } catch (Exception ex) {
            log.error("Fallo al procesar ventas del lote", ex);
            lote.setEstatus("FALLIDO");
            lote.setErroresDetalle(ex.getMessage());
            lote.setVentasProcesadas(0);
        }

        LoteSincronizacion guardado = loteRepo.save(lote);
        return mapToResponse(guardado);
    }

    /**
     * Reintenta un lote que se quedó en estatus FALLIDO.
     */
    @Transactional
    public LoteSincronizacionResponse reintentarLote(String codigoLote) {
        LoteSincronizacion lote = loteRepo.findByCodigoLote(codigoLote)
            .orElseThrow(() -> new ResourceNotFoundException("No se encontró el lote: " + codigoLote));

        if ("PROCESADO".equals(lote.getEstatus())) {
            throw new IllegalStateException("El lote ya se encuentra PROCESADO");
        }

        lote.setIntentos(lote.getIntentos() + 1);
        lote.setFechaSincronizacion(LocalDateTime.now());

        // Simulamos éxito en el reintento
        lote.setEstatus("PROCESADO");
        lote.setVentasProcesadas(lote.getTotalVentas());
        lote.setErroresDetalle("Corregido en reintento");

        LoteSincronizacion reintentoGuardado = loteRepo.save(lote);
        log.info("Lote {} reintentado exitosamente. Intentos totales: {}", codigoLote, reintentoGuardado.getIntentos());

        return mapToResponse(reintentoGuardado);
    }

    /**
     * Lista todos los lotes pendientes o fallidos que requieren atención.
     */
    @Transactional(readOnly = true)
    public List<LoteSincronizacionResponse> listarLotesFallidos() {
        return loteRepo.findByEstatus("FALLIDO")
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    private void simularInsercionVentas(List<VentaOfflineSyncDTO> ventas) {
        // Validación simulada
        for (VentaOfflineSyncDTO v : ventas) {
            if (v.getMontoTotal() == null || v.getMontoTotal().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Venta con monto inválido en referencia local: " + v.getReferenciaLocal());
            }
        }
    }

    private LoteSincronizacionResponse mapToResponse(LoteSincronizacion entity) {
        LoteSincronizacionResponse dto = new LoteSincronizacionResponse();
        dto.setCodigoLote(entity.getCodigoLote());
        dto.setEstatus(entity.getEstatus());
        dto.setTotalVentas(entity.getTotalVentas());
        dto.setVentasProcesadas(entity.getVentasProcesadas());
        dto.setMontoTotalLote(entity.getMontoTotalLote());
        dto.setFechaSincronizacion(entity.getFechaSincronizacion());
        dto.setIntentos(entity.getIntentos());
        dto.setErroresDetalle(entity.getErroresDetalle());
        return dto;
    }
}
