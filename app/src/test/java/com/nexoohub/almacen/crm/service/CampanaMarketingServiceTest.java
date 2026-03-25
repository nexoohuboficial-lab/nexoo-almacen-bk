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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CampanaMarketingServiceTest {

    @Mock
    private CampanaMarketingRepository campanaRepository;

    @Mock
    private LogEnvioMensajeRepository logEnvioRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private CampanaMarketingMapper campanaMapper;

    @InjectMocks
    private CampanaMarketingService campanaService;

    private CampanaMarketingRequest request;
    private CampanaMarketing campana;
    private CampanaMarketingResponse response;
    private Cliente clienteNormal;
    private Cliente clienteMoroso;

    @BeforeEach
    void setUp() {
        request = new CampanaMarketingRequest();
        request.setNombre("Promo Navidad");
        request.setSegmentoObjetivo("TODOS");
        request.setCanal("EMAIL");
        request.setContenidoPlantilla("Descuento del 10%");
        request.setCreadoPorUsuarioId(1);

        campana = new CampanaMarketing();
        campana.setId(1);
        campana.setNombre("Promo Navidad");
        campana.setSegmentoObjetivo("TODOS");
        campana.setEstado("BORRADOR");

        response = new CampanaMarketingResponse();
        response.setId(1);
        response.setNombre("Promo Navidad");
        response.setEstado("BORRADOR");

        clienteNormal = new Cliente();
        clienteNormal.setId(1);
        clienteNormal.setNombre("Cliente Normal");
        clienteNormal.setSaldoPendiente(BigDecimal.ZERO);

        clienteMoroso = new Cliente();
        clienteMoroso.setId(2);
        clienteMoroso.setNombre("Cliente Moroso");
        clienteMoroso.setSaldoPendiente(new BigDecimal("500.00"));
    }

    @Test
    void crearCampana() {
        when(campanaMapper.toEntity(request)).thenReturn(campana);
        when(campanaRepository.save(any(CampanaMarketing.class))).thenReturn(campana);
        when(campanaMapper.toResponse(campana)).thenReturn(response);

        CampanaMarketingResponse result = campanaService.crearCampana(request);

        assertNotNull(result);
        assertEquals("BORRADOR", result.getEstado());
        verify(campanaRepository).save(campana);
    }

    @Test
    void ejecutarCampana_TodosLosClientes() {
        when(campanaRepository.findById(1)).thenReturn(Optional.of(campana));
        when(clienteRepository.findAll()).thenReturn(List.of(clienteNormal, clienteMoroso));
        when(campanaRepository.save(any(CampanaMarketing.class))).thenReturn(campana);
        when(campanaMapper.toResponse(any(CampanaMarketing.class))).thenReturn(response);

        CampanaMarketingResponse result = campanaService.ejecutarCampana(1);

        assertNotNull(result);
        verify(logEnvioRepository, times(2)).save(any(LogEnvioMensaje.class));
        verify(campanaRepository).save(campana);
        assertEquals("FINALIZADA", campana.getEstado());
        assertEquals(2, campana.getTotalDestinatarios());
    }

    @Test
    void ejecutarCampana_SegmentoMorosos() {
        campana.setSegmentoObjetivo("MOROSOS");
        when(campanaRepository.findById(1)).thenReturn(Optional.of(campana));
        when(clienteRepository.findAll()).thenReturn(List.of(clienteNormal, clienteMoroso));
        when(campanaRepository.save(any(CampanaMarketing.class))).thenReturn(campana);
        when(campanaMapper.toResponse(any(CampanaMarketing.class))).thenReturn(response);

        campanaService.ejecutarCampana(1);

        // Solo se debió enviar al cliente moroso
        verify(logEnvioRepository, times(1)).save(any(LogEnvioMensaje.class));
        assertEquals(1, campana.getTotalDestinatarios());
    }

    @Test
    void ejecutarCampana_PrevieneDobleEjecucion() {
        campana.setEstado("FINALIZADA");
        when(campanaRepository.findById(1)).thenReturn(Optional.of(campana));

        assertThrows(IllegalStateException.class, () -> campanaService.ejecutarCampana(1));
    }

    @Test
    void obtenerMetricas() {
        campana.setEstado("FINALIZADA");
        when(campanaRepository.findById(1)).thenReturn(Optional.of(campana));
        when(logEnvioRepository.countByCampanaIdAndEstadoEnvio(1, "ENTREGADO")).thenReturn(15L);
        when(logEnvioRepository.countByCampanaIdAndEstadoEnvio(1, "FALLIDO")).thenReturn(5L);

        CampanaMetricasResponse result = campanaService.obtenerMetricas(1);

        assertNotNull(result);
        assertEquals(15L, result.getTotalEntregados());
        assertEquals(5L, result.getTotalFallidos());
        assertEquals(20L, result.getTotalEnviados());
    }
}
