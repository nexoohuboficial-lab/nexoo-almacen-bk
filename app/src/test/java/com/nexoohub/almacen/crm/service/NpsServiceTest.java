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
import com.nexoohub.almacen.catalogo.entity.Cliente;
import com.nexoohub.almacen.ventas.entity.Venta;
import com.nexoohub.almacen.ventas.repository.VentaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NpsServiceTest {

    @Mock
    private EncuestaNpsRepository encuestaRepository;

    @Mock
    private RespuestaNpsRepository respuestaRepository;

    @Mock
    private VentaRepository ventaRepository;

    @Mock
    private EncuestaNpsMapper encuestaMapper;

    @InjectMocks
    private NpsService npsService;

    private Venta venta;
    private Cliente cliente;
    private EncuestaNps encuesta;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setId(1);

        venta = new Venta();
        venta.setId(100);
        venta.setCliente(cliente);

        encuesta = new EncuestaNps();
        encuesta.setId(1);
        encuesta.setVenta(venta);
        encuesta.setVentaId(100);
        encuesta.setEnlaceUnico("UUID-1234");
        encuesta.setEstado("ENVIADA");
        encuesta.setFechaExpiracion(LocalDateTime.now().plusDays(7));
    }

    @Test
    void crearEncuesta_Exito() {
        EncuestaNpsRequest request = new EncuestaNpsRequest();
        request.setVentaId(100);

        when(encuestaRepository.existsByVentaId(100)).thenReturn(false);
        when(ventaRepository.findById(100)).thenReturn(Optional.of(venta));
        when(encuestaRepository.save(any(EncuestaNps.class))).thenReturn(encuesta);

        EncuestaNpsResponse responseDto = new EncuestaNpsResponse();
        responseDto.setEnlaceUnico("UUID-1234");
        when(encuestaMapper.toResponse(encuesta)).thenReturn(responseDto);

        EncuestaNpsResponse response = npsService.crearEncuesta(request);

        assertNotNull(response);
        assertEquals("UUID-1234", response.getEnlaceUnico());

        ArgumentCaptor<EncuestaNps> captor = ArgumentCaptor.forClass(EncuestaNps.class);
        verify(encuestaRepository).save(captor.capture());
        EncuestaNps saved = captor.getValue();
        assertEquals("ENVIADA", saved.getEstado());
        assertNotNull(saved.getEnlaceUnico());
        assertEquals(100, saved.getVentaId());
    }

    @Test
    void crearEncuesta_ThrowsCuandoYaExiste() {
        EncuestaNpsRequest request = new EncuestaNpsRequest();
        request.setVentaId(100);

        when(encuestaRepository.existsByVentaId(100)).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> npsService.crearEncuesta(request));
        assertEquals("Ya existe una encuesta para esta venta", ex.getMessage());
        verify(ventaRepository, never()).findById(any());
    }

    @Test
    void registrarRespuesta_Promotor() {
        RespuestaNpsRequest request = new RespuestaNpsRequest();
        request.setEnlaceUnico("UUID-1234");
        request.setScore(9);
        request.setComentarios("Excelente servicio");

        when(encuestaRepository.findByEnlaceUnico("UUID-1234")).thenReturn(Optional.of(encuesta));

        npsService.registrarRespuesta(request);

        ArgumentCaptor<EncuestaNps> captor = ArgumentCaptor.forClass(EncuestaNps.class);
        verify(encuestaRepository).save(captor.capture());
        
        EncuestaNps savedEncuesta = captor.getValue();
        assertEquals("RESPONDIDA", savedEncuesta.getEstado());
        
        RespuestaNps respuesta = savedEncuesta.getRespuesta();
        assertNotNull(respuesta);
        assertEquals(9, respuesta.getScore());
        assertEquals("PROMOTOR", respuesta.getClasificacion());
        assertEquals("Excelente servicio", respuesta.getComentarios());
    }

    @Test
    void registrarRespuesta_Expirada() {
        encuesta.setFechaExpiracion(LocalDateTime.now().minusDays(1));
        
        RespuestaNpsRequest request = new RespuestaNpsRequest();
        request.setEnlaceUnico("UUID-1234");
        request.setScore(10);

        when(encuestaRepository.findByEnlaceUnico("UUID-1234")).thenReturn(Optional.of(encuesta));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> npsService.registrarRespuesta(request));
        assertEquals("El enlace de la encuesta ha expirado", ex.getMessage());
        assertEquals("EXPIRADA", encuesta.getEstado());
        verify(encuestaRepository).save(encuesta); // Se debe haber guardado como expirada
    }

    @Test
    void obtenerDashboard_SinFechas() {
        when(respuestaRepository.countTotalRespuestas()).thenReturn(100L);
        when(respuestaRepository.countByClasificacion("PROMOTOR")).thenReturn(60L); // 60%
        when(respuestaRepository.countByClasificacion("PASIVO")).thenReturn(30L); // 30%
        when(respuestaRepository.countByClasificacion("DETRACTOR")).thenReturn(10L); // 10%

        NpsDashboardResponse response = npsService.obtenerDashboardDashboard();

        assertEquals(100L, response.getTotalRespuestas());
        assertEquals(60L, response.getPromotores());
        // NPS = 60% - 10% = 50.00
        assertEquals(BigDecimal.valueOf(50.0).setScale(2, RoundingMode.HALF_UP), response.getScoreNps());
    }
}
