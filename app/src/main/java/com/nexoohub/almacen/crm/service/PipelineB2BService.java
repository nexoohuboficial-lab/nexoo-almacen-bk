package com.nexoohub.almacen.crm.service;

import com.nexoohub.almacen.common.exception.BusinessException;
import com.nexoohub.almacen.common.exception.ResourceNotFoundException;
import com.nexoohub.almacen.crm.dto.*;
import com.nexoohub.almacen.crm.entity.InteraccionCrm;
import com.nexoohub.almacen.crm.entity.OportunidadVenta;
import com.nexoohub.almacen.crm.entity.Prospecto;
import com.nexoohub.almacen.crm.mapper.InteraccionCrmMapper;
import com.nexoohub.almacen.crm.mapper.OportunidadVentaMapper;
import com.nexoohub.almacen.crm.mapper.ProspectoMapper;
import com.nexoohub.almacen.crm.repository.InteraccionCrmRepository;
import com.nexoohub.almacen.crm.repository.OportunidadVentaRepository;
import com.nexoohub.almacen.crm.repository.ProspectoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PipelineB2BService {

    private final ProspectoRepository prospectoRepository;
    private final OportunidadVentaRepository oportunidadRepository;
    private final InteraccionCrmRepository interaccionRepository;
    
    private final ProspectoMapper prospectoMapper;
    private final OportunidadVentaMapper oportunidadMapper;
    private final InteraccionCrmMapper interaccionMapper;

    public PipelineB2BService(ProspectoRepository prospectoRepository,
                              OportunidadVentaRepository oportunidadRepository,
                              InteraccionCrmRepository interaccionRepository,
                              ProspectoMapper prospectoMapper,
                              OportunidadVentaMapper oportunidadMapper,
                              InteraccionCrmMapper interaccionMapper) {
        this.prospectoRepository = prospectoRepository;
        this.oportunidadRepository = oportunidadRepository;
        this.interaccionRepository = interaccionRepository;
        this.prospectoMapper = prospectoMapper;
        this.oportunidadMapper = oportunidadMapper;
        this.interaccionMapper = interaccionMapper;
    }

    // --- PROSPECTOS ---

    @Transactional
    public ProspectoResponse crearProspecto(ProspectoRequest request) {
        if (prospectoRepository.existsByRfc(request.getRfc()) && request.getRfc() != null && !request.getRfc().isBlank()) {
            throw new BusinessException("Ya existe un prospecto con el RFC: " + request.getRfc());
        }

        Prospecto prospecto = prospectoMapper.toEntity(request);
        prospecto.setEstatusViabilidad("NUEVO");
        
        prospecto = prospectoRepository.save(prospecto);
        
        return prospectoMapper.toResponse(prospecto);
    }

    @Transactional(readOnly = true)
    public List<ProspectoResponse> listarProspectos() {
        return prospectoRepository.findAll().stream()
                .map(prospectoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProspectoResponse obtenerProspecto(Integer id) {
        Prospecto prospecto = getProspectoEntity(id);
        return prospectoMapper.toResponse(prospecto);
    }

    private Prospecto getProspectoEntity(Integer id) {
        return prospectoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prospecto no encontrado con ID: " + id));
    }

    // --- OPORTUNIDADES ---

    @Transactional
    public OportunidadVentaResponse crearOportunidad(OportunidadVentaRequest request) {
        Prospecto prospecto = getProspectoEntity(request.getProspectoId());

        OportunidadVenta oportunidad = oportunidadMapper.toEntity(request);
        oportunidad.setProspecto(prospecto);
        
        oportunidad = oportunidadRepository.save(oportunidad);
        
        // Registrar interacción automática
        registrarInteraccionInterna(prospecto, oportunidad, "CREACION_OPORTUNIDAD", "Oportunidad creada automáticamente", null);

        // Actualizar estatus viabilidad de prospecto
        if ("NUEVO".equals(prospecto.getEstatusViabilidad())) {
            prospecto.setEstatusViabilidad("EN_PROGRESO");
            prospectoRepository.save(prospecto);
        }

        return oportunidadMapper.toResponse(oportunidad);
    }

    @Transactional
    public OportunidadVentaResponse cambiarEtapaOportunidad(Integer oportunidadId, CambioEtapaOportunidadRequest request) {
        OportunidadVenta oportunidad = getOportunidadEntity(oportunidadId);
        
        String etapaAnterior = oportunidad.getEtapa();
        oportunidad.setEtapa(request.getEtapa());
        
        if (request.getProbabilidadPorcentaje() != null) {
            oportunidad.setProbabilidadPorcentaje(request.getProbabilidadPorcentaje());
        }
        
        oportunidad = oportunidadRepository.save(oportunidad);
        
        // Registrar el cambio
        String resumen = String.format("Cambio de etapa: %s -> %s", etapaAnterior, request.getEtapa());
        registrarInteraccionInterna(oportunidad.getProspecto(), oportunidad, "CAMBIO_ETAPA", resumen, null);

        if ("CERRADA_GANADA".equals(request.getEtapa()) || "CERRADA_PERDIDA".equals(request.getEtapa())) {
            actualizarEstatusProspectoCierre(oportunidad.getProspecto());
        }

        return oportunidadMapper.toResponse(oportunidad);
    }
    
    private void actualizarEstatusProspectoCierre(Prospecto prospecto) {
        boolean tieneAbiertas = prospecto.getOportunidades().stream()
                .anyMatch(o -> !o.getEtapa().startsWith("CERRADA"));
                
        if (!tieneAbiertas) {
            boolean ganoAlguna = prospecto.getOportunidades().stream()
                .anyMatch(o -> "CERRADA_GANADA".equals(o.getEtapa()));
            prospecto.setEstatusViabilidad(ganoAlguna ? "CLIENTE" : "DESCARTADO");
            prospectoRepository.save(prospecto);
        }
    }

    @Transactional(readOnly = true)
    public List<OportunidadVentaResponse> listarOportunidadesPorProspecto(Integer prospectoId) {
        return oportunidadRepository.findByProspectoId(prospectoId).stream()
                .map(oportunidadMapper::toResponse)
                .collect(Collectors.toList());
    }

    private OportunidadVenta getOportunidadEntity(Integer id) {
        return oportunidadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Oportunidad no encontrada con ID: " + id));
    }

    // --- INTERACCIONES ---

    @Transactional
    public InteraccionCrmResponse registrarInteraccion(InteraccionCrmRequest request) {
        if (request.getProspectoId() == null && request.getOportunidadId() == null) {
            throw new BusinessException("Debe especificar un prospecto ID o una oportunidad ID para la interacción.");
        }

        Prospecto prospecto = null;
        if (request.getProspectoId() != null) {
            prospecto = getProspectoEntity(request.getProspectoId());
        }

        OportunidadVenta oportunidad = null;
        if (request.getOportunidadId() != null) {
            oportunidad = getOportunidadEntity(request.getOportunidadId());
            if (prospecto == null) {
                prospecto = oportunidad.getProspecto();
            } else if (!oportunidad.getProspecto().getId().equals(prospecto.getId())) {
                throw new BusinessException("La oportunidad no pertenece al prospecto especificado.");
            }
        }

        InteraccionCrm interaccion = interaccionMapper.toEntity(request);
        interaccion.setProspecto(prospecto);
        interaccion.setOportunidadVenta(oportunidad);
        
        interaccion = interaccionRepository.save(interaccion);
        
        return interaccionMapper.toResponse(interaccion);
    }

    @Transactional(readOnly = true)
    public List<InteraccionCrmResponse> listarInteraccionesPorProspecto(Integer prospectoId) {
        return interaccionRepository.findByProspectoIdOrderByFechaInteraccionDesc(prospectoId).stream()
                .map(interaccionMapper::toResponse)
                .collect(Collectors.toList());
    }

    private void registrarInteraccionInterna(Prospecto prospecto, OportunidadVenta oportunidad, String tipo, String resumen, String detalles) {
        InteraccionCrm interaccion = new InteraccionCrm();
        interaccion.setProspecto(prospecto);
        interaccion.setOportunidadVenta(oportunidad);
        interaccion.setTipoInteraccion(tipo);
        interaccion.setResumen(resumen);
        interaccion.setDetalles(detalles);
        interaccionRepository.save(interaccion);
    }
}
