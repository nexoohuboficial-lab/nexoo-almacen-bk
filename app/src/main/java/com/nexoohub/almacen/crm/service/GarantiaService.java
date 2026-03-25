package com.nexoohub.almacen.crm.service;

import com.nexoohub.almacen.common.exception.BusinessException;
import com.nexoohub.almacen.common.exception.ResourceNotFoundException;
import com.nexoohub.almacen.crm.dto.ResolucionGarantiaRequest;
import com.nexoohub.almacen.crm.dto.TicketGarantiaRequest;
import com.nexoohub.almacen.crm.dto.TicketGarantiaResponse;
import com.nexoohub.almacen.crm.entity.HistorialGarantia;
import com.nexoohub.almacen.crm.entity.TicketGarantia;
import com.nexoohub.almacen.crm.mapper.TicketGarantiaMapper;
import com.nexoohub.almacen.crm.repository.HistorialGarantiaRepository;
import com.nexoohub.almacen.crm.repository.TicketGarantiaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GarantiaService {

    private final TicketGarantiaRepository ticketGarantiaRepository;
    private final HistorialGarantiaRepository historialGarantiaRepository;
    private final TicketGarantiaMapper ticketGarantiaMapper;

    @Transactional
    public TicketGarantiaResponse abrirTicket(TicketGarantiaRequest request) {
        log.info("Abriendo ticket de garantia para el SKU: {}", request.getSkuProducto());

        TicketGarantia ticket = new TicketGarantia();
        ticket.setVentaId(request.getVentaId());
        ticket.setClienteId(request.getClienteId());
        ticket.setSkuProducto(request.getSkuProducto());
        ticket.setNumeroSerie(request.getNumeroSerie());
        ticket.setMotivoReclamo(request.getMotivoReclamo());
        ticket.setEstado("ABIERTO");
        ticket.setUsuarioCreacion(String.valueOf(request.getUsuarioAperturaId()));
        
        ticket = ticketGarantiaRepository.save(ticket);

        registrarHistorial(ticket, null, "ABIERTO", "Creación de folio de garantía comercial", request.getUsuarioAperturaId());

        return ticketGarantiaMapper.toDto(ticket);
    }

    @Transactional(readOnly = true)
    public List<TicketGarantiaResponse> getHistorialCliente(Integer clienteId) {
        List<TicketGarantia> tickets = ticketGarantiaRepository.findByClienteIdOrderByFechaCreacionDesc(clienteId);
        return ticketGarantiaMapper.toDtoList(tickets);
    }

    @Transactional(readOnly = true)
    public TicketGarantiaResponse getTicketPorId(Integer ticketId) {
        TicketGarantia ticket = ticketGarantiaRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el ticket de garantía especificado"));
        return ticketGarantiaMapper.toDto(ticket);
    }

    @Transactional
    public TicketGarantiaResponse cambiarEstado(Integer ticketId, String nuevoEstado, String comentario, Integer usuarioId) {
        TicketGarantia ticket = ticketGarantiaRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el ticket de garantía especificado"));

        if ("RESUELTO".equals(ticket.getEstado()) || "CERRADO".equals(ticket.getEstado())) {
            throw new BusinessException("No se puede cambiar el estado de un ticket que ya fue resuelto o cerrado");
        }

        String estadoAnterior = ticket.getEstado();
        ticket.setEstado(nuevoEstado);
        ticket.setUsuarioActualizacion(String.valueOf(usuarioId));
        
        ticket = ticketGarantiaRepository.save(ticket);

        registrarHistorial(ticket, estadoAnterior, nuevoEstado, comentario, usuarioId);

        return ticketGarantiaMapper.toDto(ticket);
    }

    @Transactional
    public TicketGarantiaResponse resolverTicket(Integer ticketId, ResolucionGarantiaRequest request) {
        TicketGarantia ticket = ticketGarantiaRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el ticket de garantía especificado"));

        if ("RESUELTO".equals(ticket.getEstado()) || "CERRADO".equals(ticket.getEstado())) {
            throw new BusinessException("El ticket ya fue resuelto previamente con estatus: " + ticket.getResolucion());
        }

        String estadoAnterior = ticket.getEstado();
        ticket.setEstado("RESUELTO");
        ticket.setResolucion(request.getTipoResolucion());
        ticket.setNotasInternas(request.getNotasInternas());
        ticket.setUsuarioActualizacion(String.valueOf(request.getUsuarioResolucionId()));

        ticket = ticketGarantiaRepository.save(ticket);

        registrarHistorial(ticket, estadoAnterior, "RESUELTO", 
            "Resolución dictaminada: " + request.getTipoResolucion() + " | " + request.getNotasInternas(), 
            request.getUsuarioResolucionId());

        return ticketGarantiaMapper.toDto(ticket);
    }

    private void registrarHistorial(TicketGarantia ticket, String estadoAnterior, String estadoNuevo, String comentario, Integer usuarioId) {
        HistorialGarantia historial = new HistorialGarantia();
        historial.setTicket(ticket);
        historial.setEstadoAnterior(estadoAnterior);
        historial.setEstadoNuevo(estadoNuevo);
        historial.setComentario(comentario);
        historial.setUsuarioId(usuarioId);
        
        historialGarantiaRepository.save(historial);
        // Agregamos a la lista in-memory del proxy para que se retorne en la misma transacción dentro del DTO
        ticket.getHistorial().add(historial);
    }
}
