package com.nexoohub.almacen.crm.service;

import com.nexoohub.almacen.common.exception.ResourceNotFoundException;
import com.nexoohub.almacen.crm.dto.EncuestaNpsRequest;
import com.nexoohub.almacen.crm.dto.EncuestaNpsResponse;
import com.nexoohub.almacen.crm.dto.NpsDashboardResponse;
import com.nexoohub.almacen.crm.dto.RespuestaNpsRequest;
import com.nexoohub.almacen.crm.entity.EncuestaNps;
import com.nexoohub.almacen.crm.entity.RespuestaNps;
import com.nexoohub.almacen.crm.mapper.EncuestaNpsMapper;
import com.nexoohub.almacen.crm.repository.EncuestaNpsRepository;
import com.nexoohub.almacen.crm.repository.RespuestaNpsRepository;
import com.nexoohub.almacen.ventas.entity.Venta;
import com.nexoohub.almacen.ventas.repository.VentaRepository; // TODO: Asegurar que este repositorio exista y esté importado
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class NpsService {

    private final EncuestaNpsRepository encuestaRepository;
    private final RespuestaNpsRepository respuestaRepository;
    private final VentaRepository ventaRepository;
    private final EncuestaNpsMapper encuestaMapper;

    public NpsService(EncuestaNpsRepository encuestaRepository,
                      RespuestaNpsRepository respuestaRepository,
                      VentaRepository ventaRepository,
                      EncuestaNpsMapper encuestaMapper) {
        this.encuestaRepository = encuestaRepository;
        this.respuestaRepository = respuestaRepository;
        this.ventaRepository = ventaRepository;
        this.encuestaMapper = encuestaMapper;
    }

    @Transactional
    public EncuestaNpsResponse crearEncuesta(EncuestaNpsRequest request) {
        if (encuestaRepository.existsByVentaId(request.getVentaId())) {
            throw new IllegalArgumentException("Ya existe una encuesta para esta venta");
        }

        Venta venta = ventaRepository.findById(request.getVentaId())
                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada con ID: " + request.getVentaId()));

        EncuestaNps encuesta = new EncuestaNps();
        encuesta.setVenta(venta);
        encuesta.setVentaId(venta.getId());
        
        // Asumiendo que Venta tiene getCliente() y este getCliente() no es null
        if (venta.getCliente() != null) {
            encuesta.setCliente(venta.getCliente());
            encuesta.setClienteId(venta.getCliente().getId());
        }

        encuesta.setEnlaceUnico(UUID.randomUUID().toString());
        encuesta.setEstado("ENVIADA");
        encuesta.setFechaEnvio(LocalDateTime.now());
        encuesta.setFechaExpiracion(LocalDateTime.now().plusDays(7));

        EncuestaNps savedEncuesta = encuestaRepository.save(encuesta);

        return encuestaMapper.toResponse(savedEncuesta);
    }

    @Transactional
    public void registrarRespuesta(RespuestaNpsRequest request) {
        EncuestaNps encuesta = encuestaRepository.findByEnlaceUnico(request.getEnlaceUnico())
                .orElseThrow(() -> new ResourceNotFoundException("Encuesta no encontrada o enlace inválido"));

        if (!"ENVIADA".equals(encuesta.getEstado())) {
            throw new IllegalArgumentException("La encuesta ya fue respondida o está inactiva");
        }

        if (encuesta.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            encuesta.setEstado("EXPIRADA");
            encuestaRepository.save(encuesta);
            throw new IllegalArgumentException("El enlace de la encuesta ha expirado");
        }

        RespuestaNps respuesta = new RespuestaNps();
        respuesta.setEncuesta(encuesta);
        respuesta.setEncuestaId(encuesta.getId());
        respuesta.setScore(request.getScore());
        respuesta.setComentarios(request.getComentarios());
        respuesta.setFechaRespuesta(LocalDateTime.now());

        if (request.getScore() <= 6) {
            respuesta.setClasificacion("DETRACTOR");
        } else if (request.getScore() <= 8) {
            respuesta.setClasificacion("PASIVO");
        } else {
            respuesta.setClasificacion("PROMOTOR");
        }

        encuesta.setEstado("RESPONDIDA");
        encuesta.setRespuesta(respuesta);

        // Al usar CascadeType.ALL en encuesta -> respuesta, no es estrictamente necesario guardar respuesta primero
        encuestaRepository.save(encuesta);
    }

    @Transactional(readOnly = true)
    public NpsDashboardResponse obtenerDashboardDashboard() {
        return obtenerDashboard(null, null); // Placeholder para fechas; opcional
    }

    @Transactional(readOnly = true)
    public NpsDashboardResponse obtenerDashboard(LocalDateTime inicio, LocalDateTime fin) {
        long total;
        long promotores;
        long pasivos;
        long detractores;

        if (inicio != null && fin != null) {
            total = respuestaRepository.countTotalRespuestas(); // Aquí podríamos filtrar por fechas también si el repositorio lo expone
            promotores = respuestaRepository.countByClasificacionAndDateRange("PROMOTOR", inicio, fin);
            pasivos = respuestaRepository.countByClasificacionAndDateRange("PASIVO", inicio, fin);
            detractores = respuestaRepository.countByClasificacionAndDateRange("DETRACTOR", inicio, fin);
            // Re-calcular total por rango para ser precisos
            total = promotores + pasivos + detractores;
        } else {
            total = respuestaRepository.countTotalRespuestas();
            promotores = respuestaRepository.countByClasificacion("PROMOTOR");
            pasivos = respuestaRepository.countByClasificacion("PASIVO");
            detractores = respuestaRepository.countByClasificacion("DETRACTOR");
        }

        BigDecimal scoreNps = BigDecimal.ZERO;
        if (total > 0) {
            double pPromotores = (double) promotores / total * 100;
            double pDetractores = (double) detractores / total * 100;
            scoreNps = BigDecimal.valueOf(pPromotores - pDetractores)
                    .setScale(2, RoundingMode.HALF_UP);
        }

        NpsDashboardResponse response = new NpsDashboardResponse();
        response.setTotalRespuestas(total);
        response.setPromotores(promotores);
        response.setPasivos(pasivos);
        response.setDetractores(detractores);
        response.setScoreNps(scoreNps);

        return response;
    }
}
