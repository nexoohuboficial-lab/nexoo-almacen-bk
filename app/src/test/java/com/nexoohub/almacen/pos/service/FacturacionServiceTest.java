package com.nexoohub.almacen.pos.service;

import com.nexoohub.almacen.common.exception.ResourceNotFoundException;
import com.nexoohub.almacen.pos.dto.CancelacionCfdiRequest;
import com.nexoohub.almacen.pos.dto.FacturaFiscalResponse;
import com.nexoohub.almacen.pos.dto.TimbradoRequest;
import com.nexoohub.almacen.pos.entity.ConfigPac;
import com.nexoohub.almacen.pos.entity.FacturaFiscal;
import com.nexoohub.almacen.pos.repository.ConfigPacRepository;
import com.nexoohub.almacen.pos.repository.FacturaFiscalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FacturacionService — POS-03 Pruebas Unitarias")
class FacturacionServiceTest {

    @Mock
    private FacturaFiscalRepository facturaRepo;

    @Mock
    private ConfigPacRepository pacRepo;

    @InjectMocks
    private FacturacionService facturacionService;

    private TimbradoRequest timbradoReq;
    private CancelacionCfdiRequest cancelReq;
    private ConfigPac configPac;
    private FacturaFiscal facturaTimbrada;

    @BeforeEach
    void setUp() {
        timbradoReq = new TimbradoRequest();
        timbradoReq.setVentaId(1);
        timbradoReq.setClienteId(10);
        timbradoReq.setMontoTotal(new BigDecimal("1500.00"));
        timbradoReq.setUsoCfdi("G03");
        timbradoReq.setMetodoPago("PUE");
        timbradoReq.setFormaPago("01");
        timbradoReq.setRfcReceptor("XAXX010101000");
        timbradoReq.setRazonSocialReceptor("PUBLICO EN GENERAL");
        timbradoReq.setCodigoPostalReceptor("00000");
        timbradoReq.setRegimenFiscalReceptor("616");

        configPac = new ConfigPac();
        configPac.setId(1);
        configPac.setProveedor("FACTURAPI");
        configPac.setEntorno("PRUEBAS");

        facturaTimbrada = new FacturaFiscal();
        facturaTimbrada.setId(100);
        facturaTimbrada.setVentaId(1);
        facturaTimbrada.setClienteId(10);
        facturaTimbrada.setUuid("F2A1E9E0-1234-4567-890A-B1C2D3E4F5G6");
        facturaTimbrada.setEstatus("TIMBRADA");
        facturaTimbrada.setMontoTotal(new BigDecimal("1500.00"));

        cancelReq = new CancelacionCfdiRequest();
        cancelReq.setMotivoCancelacion("02");
    }

    @Test
    @DisplayName("timbrarFactura → éxito: crea y simula UUID")
    void timbrarFactura_exito() {
        when(facturaRepo.findByVentaId(1)).thenReturn(Optional.empty());
        when(pacRepo.findActiveConfiguration()).thenReturn(Optional.of(configPac));
        
        when(facturaRepo.save(any(FacturaFiscal.class))).thenAnswer(i -> {
            FacturaFiscal f = i.getArgument(0);
            f.setId(200);
            f.setFechaEmision(LocalDateTime.now());
            return f;
        });

        FacturaFiscalResponse response = facturacionService.timbrarFactura(timbradoReq);

        assertThat(response.getEstatus()).isEqualTo("TIMBRADA");
        assertThat(response.getUuid()).isNotNull();
        assertThat(response.getUrlPdf()).contains(response.getUuid());
        verify(facturaRepo).save(any(FacturaFiscal.class));
    }

    @Test
    @DisplayName("timbrarFactura → error: venta ya facturada")
    void timbrarFactura_ventaFacturada_lanzaExcepcion() {
        when(facturaRepo.findByVentaId(1)).thenReturn(Optional.of(facturaTimbrada));

        assertThatThrownBy(() -> facturacionService.timbrarFactura(timbradoReq))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("ya cuenta con una factura");

        verify(pacRepo, never()).findActiveConfiguration();
    }

    @Test
    @DisplayName("timbrarFactura → error: sin PAC configurado")
    void timbrarFactura_sinPac_lanzaExcepcion() {
        when(facturaRepo.findByVentaId(1)).thenReturn(Optional.empty());
        when(pacRepo.findActiveConfiguration()).thenReturn(Optional.empty());

        assertThatThrownBy(() -> facturacionService.timbrarFactura(timbradoReq))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("No hay un PAC configurado");
    }

    @Test
    @DisplayName("cancelarFactura → éxito")
    void cancelarFactura_exito() {
        when(facturaRepo.findById(100)).thenReturn(Optional.of(facturaTimbrada));
        when(facturaRepo.save(any(FacturaFiscal.class))).thenAnswer(i -> i.getArgument(0));

        FacturaFiscalResponse response = facturacionService.cancelarFactura(100, cancelReq);

        assertThat(response.getEstatus()).isEqualTo("CANCELADA");
        assertThat(response.getMotivoCancelacion()).isEqualTo("02");
        assertThat(response.getAcuseCancelacion()).contains("Cancelado");
    }

    @Test
    @DisplayName("cancelarFactura → error: factura no encontrada")
    void cancelarFactura_noExiste_lanzaExcepcion() {
        when(facturaRepo.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> facturacionService.cancelarFactura(999, cancelReq))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Factura no encontrada");
    }

    @Test
    @DisplayName("cancelarFactura → error: no está TIMBRADA")
    void cancelarFactura_noTimbrada_lanzaExcepcion() {
        facturaTimbrada.setEstatus("CANCELADA");
        when(facturaRepo.findById(100)).thenReturn(Optional.of(facturaTimbrada));

        assertThatThrownBy(() -> facturacionService.cancelarFactura(100, cancelReq))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("no está TIMBRADA");
    }

    @Test
    @DisplayName("descargarFactura → éxito")
    void descargarFactura_exito() {
        when(facturaRepo.findById(100)).thenReturn(Optional.of(facturaTimbrada));

        FacturaFiscalResponse response = facturacionService.descargarFactura(100);
        assertThat(response.getUuid()).isEqualTo(facturaTimbrada.getUuid());
    }

    @Test
    @DisplayName("consultarFacturasPorCliente → retorna lista")
    void consultarFacturasPorCliente_exito() {
        when(facturaRepo.findByClienteIdOrderByFechaEmisionDesc(10))
            .thenReturn(Collections.singletonList(facturaTimbrada));

        List<FacturaFiscalResponse> result = facturacionService.consultarFacturasPorCliente(10);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUuid()).isEqualTo(facturaTimbrada.getUuid());
    }
}
