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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PipelineB2BServiceTest {

    @Mock
    private ProspectoRepository prospectoRepository;
    @Mock
    private OportunidadVentaRepository oportunidadRepository;
    @Mock
    private InteraccionCrmRepository interaccionRepository;
    @Mock
    private ProspectoMapper prospectoMapper;
    @Mock
    private OportunidadVentaMapper oportunidadMapper;
    @Mock
    private InteraccionCrmMapper interaccionMapper;

    @InjectMocks
    private PipelineB2BService pipelineService;

    private Prospecto prospectoMock;
    private ProspectoRequest prospectoReq;

    @BeforeEach
    void setUp() {
        prospectoMock = new Prospecto();
        prospectoMock.setId(1);
        prospectoMock.setRfc("XAXX010101000");
        prospectoMock.setEstatusViabilidad("NUEVO");

        prospectoReq = new ProspectoRequest();
        prospectoReq.setRfc("XAXX010101000");
    }

    @Test
    void crearProspecto_ThrowsBusinessException_WhenRfcExists() {
        when(prospectoRepository.existsByRfc("XAXX010101000")).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class, () -> 
            pipelineService.crearProspecto(prospectoReq)
        );
        assertTrue(exception.getMessage().contains("Ya existe un prospecto con el RFC"));
        verify(prospectoRepository, never()).save(any());
    }

    @Test
    void crearProspecto_Success() {
        when(prospectoRepository.existsByRfc("XAXX010101000")).thenReturn(false);
        when(prospectoMapper.toEntity(prospectoReq)).thenReturn(prospectoMock);
        when(prospectoRepository.save(any(Prospecto.class))).thenReturn(prospectoMock);
        
        ProspectoResponse resMock = new ProspectoResponse();
        resMock.setId(1);
        when(prospectoMapper.toResponse(prospectoMock)).thenReturn(resMock);

        ProspectoResponse result = pipelineService.crearProspecto(prospectoReq);

        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(prospectoRepository).save(any(Prospecto.class));
    }

    @Test
    void crearOportunidad_SuccessAndUpdatesProspecto() {
        OportunidadVentaRequest opReq = new OportunidadVentaRequest();
        opReq.setProspectoId(1);
        
        OportunidadVenta opMock = new OportunidadVenta();
        opMock.setId(10);
        opMock.setProspecto(prospectoMock);

        when(prospectoRepository.findById(1)).thenReturn(Optional.of(prospectoMock));
        when(oportunidadMapper.toEntity(opReq)).thenReturn(opMock);
        when(oportunidadRepository.save(any(OportunidadVenta.class))).thenReturn(opMock);
        
        OportunidadVentaResponse resMock = new OportunidadVentaResponse();
        resMock.setId(10);
        when(oportunidadMapper.toResponse(opMock)).thenReturn(resMock);

        pipelineService.crearOportunidad(opReq);

        // Verify Prospecto status was updated
        assertEquals("EN_PROGRESO", prospectoMock.getEstatusViabilidad());
        verify(prospectoRepository).save(prospectoMock);
        
        // Verify Interaccion was created automatically
        verify(interaccionRepository).save(any(InteraccionCrm.class));
    }

    @Test
    void crearOportunidad_ThrowsNotFound_WhenProspectoMissing() {
        OportunidadVentaRequest opReq = new OportunidadVentaRequest();
        opReq.setProspectoId(99);
        
        when(prospectoRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> pipelineService.crearOportunidad(opReq));
    }

    @Test
    void registrarInteraccion_ThrowsBusinessException_WhenNoIdsProvided() {
        InteraccionCrmRequest req = new InteraccionCrmRequest();
        
        assertThrows(BusinessException.class, () -> pipelineService.registrarInteraccion(req));
    }

    @Test
    void cambiarEtapaOportunidad_CierraGanada_ConvierteEnCliente() {
        OportunidadVenta opMock = new OportunidadVenta();
        opMock.setId(10);
        opMock.setEtapa("NEGOCIACION");
        opMock.setProspecto(prospectoMock);
        prospectoMock.getOportunidades().add(opMock);

        CambioEtapaOportunidadRequest req = new CambioEtapaOportunidadRequest();
        req.setEtapa("CERRADA_GANADA");
        
        when(oportunidadRepository.findById(10)).thenReturn(Optional.of(opMock));
        when(oportunidadRepository.save(any())).thenReturn(opMock);

        pipelineService.cambiarEtapaOportunidad(10, req);

        assertEquals("CLIENTE", prospectoMock.getEstatusViabilidad());
        verify(prospectoRepository).save(prospectoMock);
        verify(interaccionRepository).save(any(InteraccionCrm.class)); // El log de cambio de etapa
    }
}
