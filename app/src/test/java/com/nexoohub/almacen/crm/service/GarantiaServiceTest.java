package com.nexoohub.almacen.crm.service;

import com.nexoohub.almacen.common.exception.BusinessException;
import com.nexoohub.almacen.crm.dto.ResolucionGarantiaRequest;
import com.nexoohub.almacen.crm.dto.TicketGarantiaRequest;
import com.nexoohub.almacen.crm.dto.TicketGarantiaResponse;
import com.nexoohub.almacen.crm.entity.HistorialGarantia;
import com.nexoohub.almacen.crm.entity.TicketGarantia;
import com.nexoohub.almacen.crm.mapper.TicketGarantiaMapper;
import com.nexoohub.almacen.crm.repository.HistorialGarantiaRepository;
import com.nexoohub.almacen.crm.repository.TicketGarantiaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GarantiaServiceTest {

    @Mock
    private TicketGarantiaRepository ticketGarantiaRepository;

    @Mock
    private HistorialGarantiaRepository historialGarantiaRepository;

    @Mock
    private TicketGarantiaMapper ticketGarantiaMapper;

    @InjectMocks
    private GarantiaService garantiaService;

    private TicketGarantia ticketSimulado;
    private TicketGarantiaResponse responseSimulado;

    @BeforeEach
    void setUp() {
        ticketSimulado = new TicketGarantia();
        ticketSimulado.setId(1);
        ticketSimulado.setEstado("ABIERTO");
        ticketSimulado.setSkuProducto("SKU-123");
        ticketSimulado.setHistorial(new ArrayList<>());

        responseSimulado = new TicketGarantiaResponse();
        responseSimulado.setId(1);
        responseSimulado.setEstado("ABIERTO");
        responseSimulado.setSkuProducto("SKU-123");
    }

    @Test
    void abrirTicket_Exito() {
        TicketGarantiaRequest request = new TicketGarantiaRequest();
        request.setSkuProducto("SKU-123");
        request.setMotivoReclamo("Falla en pantalla");
        request.setUsuarioAperturaId(99);

        when(ticketGarantiaRepository.save(any(TicketGarantia.class))).thenReturn(ticketSimulado);
        when(ticketGarantiaMapper.toDto(any(TicketGarantia.class))).thenReturn(responseSimulado);

        TicketGarantiaResponse result = garantiaService.abrirTicket(request);

        assertNotNull(result);
        assertEquals("SKU-123", result.getSkuProducto());
        assertEquals("ABIERTO", result.getEstado());

        verify(ticketGarantiaRepository).save(any(TicketGarantia.class));
        verify(historialGarantiaRepository).save(any(HistorialGarantia.class));
    }

    @Test
    void cambiarEstado_Exito() {
        when(ticketGarantiaRepository.findById(1)).thenReturn(Optional.of(ticketSimulado));
        when(ticketGarantiaRepository.save(any(TicketGarantia.class))).thenReturn(ticketSimulado);
        
        TicketGarantiaResponse responseModificado = new TicketGarantiaResponse();
        responseModificado.setEstado("EN_REVISION");
        when(ticketGarantiaMapper.toDto(any(TicketGarantia.class))).thenReturn(responseModificado);

        TicketGarantiaResponse result = garantiaService.cambiarEstado(1, "EN_REVISION", "Revisión técnica", 99);

        assertEquals("EN_REVISION", result.getEstado());
        verify(historialGarantiaRepository).save(any(HistorialGarantia.class));
    }

    @Test
    void cambiarEstado_FallaSiYaEstaResuelto() {
        ticketSimulado.setEstado("RESUELTO");
        when(ticketGarantiaRepository.findById(1)).thenReturn(Optional.of(ticketSimulado));

        BusinessException excepcion = assertThrows(BusinessException.class, () ->
                garantiaService.cambiarEstado(1, "EN_REVISION", "Revisión", 99)
        );

        assertTrue(excepcion.getMessage().contains("ya fue resuelto o cerrado"));
        verify(ticketGarantiaRepository, never()).save(any());
    }

    @Test
    void resolverTicket_Exito() {
        when(ticketGarantiaRepository.findById(1)).thenReturn(Optional.of(ticketSimulado));
        when(ticketGarantiaRepository.save(any(TicketGarantia.class))).thenReturn(ticketSimulado);

        ResolucionGarantiaRequest request = new ResolucionGarantiaRequest();
        request.setTipoResolucion("CAMBIO_PIEZA");
        request.setNotasInternas("Se cambió el display");
        request.setUsuarioResolucionId(100);

        TicketGarantiaResponse responseModificado = new TicketGarantiaResponse();
        responseModificado.setEstado("RESUELTO");
        responseModificado.setResolucion("CAMBIO_PIEZA");
        when(ticketGarantiaMapper.toDto(any(TicketGarantia.class))).thenReturn(responseModificado);

        TicketGarantiaResponse result = garantiaService.resolverTicket(1, request);

        assertEquals("RESUELTO", result.getEstado());
        assertEquals("CAMBIO_PIEZA", result.getResolucion());
        verify(historialGarantiaRepository).save(any(HistorialGarantia.class));
    }
}
