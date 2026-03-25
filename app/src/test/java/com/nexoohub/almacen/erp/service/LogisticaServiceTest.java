package com.nexoohub.almacen.erp.service;

import com.nexoohub.almacen.common.exception.ResourceNotFoundException;
import com.nexoohub.almacen.erp.dto.RutaEntregaRequest;
import com.nexoohub.almacen.erp.dto.RutaEntregaResponse;
import com.nexoohub.almacen.erp.dto.RutaFacturaRequest;
import com.nexoohub.almacen.erp.entity.Chofer;
import com.nexoohub.almacen.erp.entity.RutaEntrega;
import com.nexoohub.almacen.erp.entity.Vehiculo;
import com.nexoohub.almacen.erp.repository.ChoferRepository;
import com.nexoohub.almacen.erp.repository.RutaEntregaRepository;
import com.nexoohub.almacen.erp.repository.RutaFacturaRepository;
import com.nexoohub.almacen.erp.repository.VehiculoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LogisticaService — ERP-03 Pruebas Unitarias")
class LogisticaServiceTest {

    @Mock
    private VehiculoRepository vehiculoRepo;
    
    @Mock
    private ChoferRepository choferRepo;
    
    @Mock
    private RutaEntregaRepository rutaRepo;
    
    @Mock
    private RutaFacturaRepository rutaFacturaRepo;

    @InjectMocks
    private LogisticaService logisticaService;

    private Chofer chofer;
    private Vehiculo vehiculo;
    private RutaEntrega ruta;

    @BeforeEach
    void setUp() {
        chofer = new Chofer();
        chofer.setId(1);
        chofer.setNombreCompleto("Juan Perez");
        
        vehiculo = new Vehiculo();
        vehiculo.setId(2);
        vehiculo.setPlacas("XYZ-123");

        ruta = new RutaEntrega();
        ruta.setId(100);
        ruta.setCodigoRuta("RUT-001");
        ruta.setEstatus("PENDIENTE");
    }

    @Test
    @DisplayName("crearRuta → Éxito con Flotilla Propia")
    void crearRuta_FlotillaPropia_Exito() {
        RutaEntregaRequest req = new RutaEntregaRequest();
        req.setCodigoRuta("RUT-001");
        req.setFechaProgramada(LocalDate.now());
        req.setEsPaqueteria(false);
        req.setChoferId(1);
        req.setVehiculoId(2);
        req.setUsuarioId(1);

        when(rutaRepo.findByCodigoRuta(anyString())).thenReturn(Optional.empty());
        when(choferRepo.findById(1)).thenReturn(Optional.of(chofer));
        when(vehiculoRepo.findById(2)).thenReturn(Optional.of(vehiculo));
        when(rutaRepo.save(any(RutaEntrega.class))).thenReturn(ruta);

        RutaEntregaResponse resp = logisticaService.crearRuta(req);
        
        assertThat(resp).isNotNull();
        verify(rutaRepo).save(any(RutaEntrega.class));
    }

    @Test
    @DisplayName("crearRuta → Éxito con Paquetería Externa (ML)")
    void crearRuta_Paqueteria_Exito() {
        RutaEntregaRequest req = new RutaEntregaRequest();
        req.setCodigoRuta("RUT-ML-001");
        req.setFechaProgramada(LocalDate.now());
        req.setEsPaqueteria(true);
        req.setProveedorEnvio("MERCADO_LIBRE");
        req.setUsuarioId(1);

        RutaEntrega rutaMl = new RutaEntrega();
        rutaMl.setId(101);
        rutaMl.setCodigoRuta("RUT-ML-001");
        rutaMl.setEsPaqueteria(true);
        rutaMl.setProveedorEnvio("MERCADO_LIBRE");

        when(rutaRepo.findByCodigoRuta(anyString())).thenReturn(Optional.empty());
        when(rutaRepo.save(any(RutaEntrega.class))).thenReturn(rutaMl);

        RutaEntregaResponse resp = logisticaService.crearRuta(req);
        
        assertThat(resp.getEsPaqueteria()).isTrue();
        assertThat(resp.getProveedorEnvio()).isEqualTo("MERCADO_LIBRE");
        verify(rutaRepo).save(any(RutaEntrega.class));
    }
    
    @Test
    @DisplayName("crearRuta → Error por falta de proveedor en paquetería")
    void crearRuta_Paqueteria_SinProveedor() {
        RutaEntregaRequest req = new RutaEntregaRequest();
        req.setCodigoRuta("RUT-ERR");
        req.setEsPaqueteria(true);
        req.setFechaProgramada(LocalDate.now());

        when(rutaRepo.findByCodigoRuta("RUT-ERR")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> logisticaService.crearRuta(req))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("debe indicar el proveedor");
    }

    @Test
    @DisplayName("asignarFactura → Agrega paquete con número de Guía")
    void asignarFactura_ConGuia_Exito() {
        RutaFacturaRequest req = new RutaFacturaRequest();
        req.setFacturaClienteId(500);
        req.setNumeroGuia("ML123456789");
        req.setUrlRastreo("https://mercadolibre.com/rastreo");

        when(rutaRepo.findById(100)).thenReturn(Optional.of(ruta));
        when(rutaRepo.save(any())).thenReturn(ruta); // Simplificado para prueba

        RutaEntregaResponse resp = logisticaService.asignarFactura(100, req);
        
        assertThat(ruta.getFacturas()).hasSize(1);
        assertThat(ruta.getFacturas().get(0).getNumeroGuia()).isEqualTo("ML123456789");
    }

    @Test
    @DisplayName("cambiarEstatusRuta → Exito transicionando a EN_TRANSITO")
    void cambiarEstatusRuta_Exito() {
        when(rutaRepo.findById(100)).thenReturn(Optional.of(ruta));
        when(rutaRepo.save(any())).thenReturn(ruta);

        logisticaService.cambiarEstatusRuta(100, "EN_TRANSITO");
        
        assertThat(ruta.getEstatus()).isEqualTo("EN_TRANSITO");
    }
}
