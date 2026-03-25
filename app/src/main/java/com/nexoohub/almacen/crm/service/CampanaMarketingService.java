package com.nexoohub.almacen.crm.service;

import com.nexoohub.almacen.catalogo.entity.Cliente;
import com.nexoohub.almacen.catalogo.repository.ClienteRepository;
import com.nexoohub.almacen.crm.dto.CampanaMarketingRequest;
import com.nexoohub.almacen.crm.dto.CampanaMarketingResponse;
import com.nexoohub.almacen.crm.dto.CampanaMetricasResponse;
import com.nexoohub.almacen.crm.entity.CampanaMarketing;
import com.nexoohub.almacen.crm.entity.LogEnvioMensaje;
import com.nexoohub.almacen.crm.mapper.CampanaMarketingMapper;
import com.nexoohub.almacen.crm.repository.CampanaMarketingRepository;
import com.nexoohub.almacen.crm.repository.LogEnvioMensajeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CampanaMarketingService {

    private final CampanaMarketingRepository campanaRepository;
    private final LogEnvioMensajeRepository logEnvioRepository;
    private final ClienteRepository clienteRepository;
    private final CampanaMarketingMapper campanaMapper;

    public CampanaMarketingService(
            CampanaMarketingRepository campanaRepository,
            LogEnvioMensajeRepository logEnvioRepository,
            ClienteRepository clienteRepository,
            CampanaMarketingMapper campanaMapper) {
        this.campanaRepository = campanaRepository;
        this.logEnvioRepository = logEnvioRepository;
        this.clienteRepository = clienteRepository;
        this.campanaMapper = campanaMapper;
    }

    @Transactional
    public CampanaMarketingResponse crearCampana(CampanaMarketingRequest request) {
        CampanaMarketing entidad = campanaMapper.toEntity(request);
        entidad.setEstado("BORRADOR");
        
        entidad = campanaRepository.save(entidad);
        return campanaMapper.toResponse(entidad);
    }

    @Transactional
    public CampanaMarketingResponse ejecutarCampana(Integer id) {
        CampanaMarketing campana = campanaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Campaña no encontrada"));

        if ("FINALIZADA".equals(campana.getEstado()) || "EN_PROGRESO".equals(campana.getEstado())) {
            throw new IllegalStateException("La campaña ya fue ejecutada o está en progreso");
        }

        campana.setEstado("EN_PROGRESO");
        campana.setFechaEjecucion(LocalDateTime.now());
        
        List<Cliente> clientesObjetivo = obtenerClientesPorSegmento(campana.getSegmentoObjetivo());
        campana.setTotalDestinatarios(clientesObjetivo.size());

        for (Cliente cliente : clientesObjetivo) {
            boolean simulacionFalla = Math.random() < 0.05; // 5% de probabilidad de fallo simulado
            
            LogEnvioMensaje log = new LogEnvioMensaje();
            log.setCampana(campana);
            log.setCliente(cliente);
            log.setTelefonoDestino(cliente.getTelefono());
            log.setEmailDestino(cliente.getEmail());
            log.setFechaEnvio(LocalDateTime.now());
            
            if (simulacionFalla) {
                log.setEstadoEnvio("FALLIDO");
                log.setMensajeError("TimeOut simulado al contactar con el proveedor.");
            } else {
                log.setEstadoEnvio("ENTREGADO");
                log.setFechaEntrega(LocalDateTime.now().plusSeconds(1));
            }
            
            logEnvioRepository.save(log);
        }

        campana.setEstado("FINALIZADA");
        campana = campanaRepository.save(campana);
        
        return campanaMapper.toResponse(campana);
    }

    @Transactional(readOnly = true)
    public CampanaMetricasResponse obtenerMetricas(Integer id) {
        CampanaMarketing campana = campanaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Campaña no encontrada"));

        long totales = logEnvioRepository.countByCampanaIdAndEstadoEnvio(id, "ENTREGADO") + 
                       logEnvioRepository.countByCampanaIdAndEstadoEnvio(id, "FALLIDO");
                       
        long entregados = logEnvioRepository.countByCampanaIdAndEstadoEnvio(id, "ENTREGADO");
        long fallidos = logEnvioRepository.countByCampanaIdAndEstadoEnvio(id, "FALLIDO");

        return new CampanaMetricasResponse(
                campana.getId(),
                campana.getNombre(),
                campana.getEstado(),
                totales,
                entregados,
                fallidos
        );
    }

    private List<Cliente> obtenerClientesPorSegmento(String segmento) {
        List<Cliente> todos = clienteRepository.findAll();
        
        if (segmento == null || segmento.trim().isEmpty() || "TODOS".equalsIgnoreCase(segmento)) {
            return todos;
        }
        
        if ("PLATINO".equalsIgnoreCase(segmento)) {
            return todos.stream()
                        .filter(c -> c.getTipoClienteId() != null && c.getTipoClienteId() == 1) // Suponiendo ID 1 es PLATINO
                        .collect(Collectors.toList());
        }
        
        if ("MOROSOS".equalsIgnoreCase(segmento)) {
             return todos.stream()
                        .filter(c -> c.getSaldoPendiente() != null && c.getSaldoPendiente().compareTo(java.math.BigDecimal.ZERO) > 0)
                        .collect(Collectors.toList());
        }

        return todos; // Fallback
    }
}
