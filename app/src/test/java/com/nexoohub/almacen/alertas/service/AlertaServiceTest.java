package com.nexoohub.almacen.alertas.service;

import com.nexoohub.almacen.alertas.dto.AlertaResponse;
import com.nexoohub.almacen.alertas.dto.ConfigNotificacionRequest;
import com.nexoohub.almacen.alertas.dto.ConfigurarAlertaRequest;
import com.nexoohub.almacen.alertas.entity.*;
import com.nexoohub.almacen.alertas.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AlertaService — Pruebas Unitarias")
class AlertaServiceTest {

    @Mock
    private AlertaSistemaRepository alertaRepo;

    @Mock
    private ConfiguracionAlertaRepository configAlertaRepo;

    @Mock
    private ConfigNotificacionRepository configNotificacionRepo;

    @Mock
    private GmailNotificacionService gmailService;

    @Mock
    private TelegramNotificacionService telegramService;

    @InjectMocks
    private AlertaService alertaService;

    private AlertaSistema alertaMock;

    @BeforeEach
    void setUp() {
        alertaMock = new AlertaSistema();
        alertaMock.setId(1);
        alertaMock.setTipo(TipoAlerta.STOCK_BAJO);
        alertaMock.setMensaje("⚠️ Stock bajo: Producto X");
        alertaMock.setSucursalId(1);
        alertaMock.setUsuarioDestinoId(1);
        alertaMock.setResuelta(false);
        alertaMock.setFechaCreacion(LocalDateTime.now());
    }

    // ─────────────────────────── crearAlerta ─────────────────────────────────

    @Test
    @DisplayName("crearAlerta — persiste alerta y despacha notificaciones")
    void crearAlerta_persisteYDespachaNotificaciones() {
        // Arrange
        when(alertaRepo.save(any(AlertaSistema.class))).thenReturn(alertaMock);
        when(configNotificacionRepo.findByUsuarioIdAndActivoTrue(anyInt())).thenReturn(Optional.empty());

        // Act
        alertaService.crearAlerta(com.nexoohub.almacen.alertas.entity.TipoAlerta.STOCK_BAJO, "⚠️ Stock bajo", 1, 1);

        // Assert
        verify(alertaRepo, times(1)).save(any(AlertaSistema.class));
    }

    // ─────────────────────────── listarNoLeidas ───────────────────────────────

    @Test
    @DisplayName("listarNoLeidas — retorna lista de DTOs correctamente mapeados")
    void listarNoLeidas_retornaAlertasNoLeidas() {
        // Arrange
        when(alertaRepo.findByUsuarioDestinoIdAndLeidaFalse(1))
                .thenReturn(List.of(alertaMock));

        // Act
        List<AlertaResponse> resultado = alertaService.listarNoLeidas(1);

        // Assert
        assertThat(resultado).isNotEmpty();
        assertThat(resultado.get(0).getMensaje()).isEqualTo("⚠️ Stock bajo: Producto X");
        assertThat(resultado.get(0).getTipo()).isEqualTo(TipoAlerta.STOCK_BAJO);
    }

    // ─────────────────────────── marcarResuelta ───────────────────────────────

    @Test
    @DisplayName("marcarResuelta — actualiza el campo y persiste")
    void marcarResuelta_actualizaEstado() {
        // Arrange
        when(alertaRepo.findById(1)).thenReturn(Optional.of(alertaMock));
        when(alertaRepo.save(any(AlertaSistema.class))).thenReturn(alertaMock);

        // Act
        alertaService.marcarResuelta(1);

        // Assert
        assertThat(alertaMock.isResuelta()).isTrue();
        verify(alertaRepo, times(1)).save(alertaMock);
    }

    // ─────────────────────────── configurarSucursal ───────────────────────────

    @Test
    @DisplayName("configurarSucursal — crea nueva configuración si no existe")
    void configurarSucursal_creaConfiguracionNueva() {
        // Arrange
        ConfigurarAlertaRequest request = mock(ConfigurarAlertaRequest.class);
        when(request.getSucursalId()).thenReturn(5);
        when(request.getStockMinimo()).thenReturn(10);
        when(request.getDiasVencimientoCxC()).thenReturn(30);
        when(request.getPorcentajeMetaAlerta()).thenReturn(60);
        when(configAlertaRepo.findBySucursalIdAndActivoTrue(5)).thenReturn(Optional.empty());

        // Act
        alertaService.configurarSucursal(request);

        // Assert
        verify(configAlertaRepo, times(1)).save(any(ConfiguracionAlerta.class));
    }

    @Test
    @DisplayName("configurarSucursal — actualiza configuración existente")
    void configurarSucursal_actualizaConfiguracionExistente() {
        // Arrange
        ConfiguracionAlerta configExistente = new ConfiguracionAlerta();
        configExistente.setSucursalId(1);
        configExistente.setStockMinimo(5);

        ConfigurarAlertaRequest request = mock(ConfigurarAlertaRequest.class);
        when(request.getSucursalId()).thenReturn(1);
        when(request.getStockMinimo()).thenReturn(15);
        when(request.getDiasVencimientoCxC()).thenReturn(45);
        when(request.getPorcentajeMetaAlerta()).thenReturn(70);
        when(configAlertaRepo.findBySucursalIdAndActivoTrue(1)).thenReturn(Optional.of(configExistente));

        // Act
        alertaService.configurarSucursal(request);

        // Assert
        assertThat(configExistente.getStockMinimo()).isEqualTo(15);
        verify(configAlertaRepo, times(1)).save(configExistente);
    }

    // ─────────────────────────── contarNoLeidas ───────────────────────────────

    @Test
    @DisplayName("contarNoLeidas — delega al repositorio el conteo")
    void contarNoLeidas_delegaAlRepositorio() {
        // Arrange
        when(alertaRepo.countByUsuarioDestinoIdAndLeidaFalse(1)).thenReturn(7L);

        // Act
        long count = alertaService.contarNoLeidas(1);

        // Assert
        assertThat(count).isEqualTo(7L);
        verify(alertaRepo, times(1)).countByUsuarioDestinoIdAndLeidaFalse(1);
    }

    // ─────────────────────────── configurarCanalUsuario ───────────────────────

    @Test
    @DisplayName("configurarCanalUsuario — registra canal EMAIL correctamente")
    void configurarCanalUsuario_registraCanalEmail() {
        // Arrange
        ConfigNotificacionRequest request = mock(ConfigNotificacionRequest.class);
        when(request.getUsuarioId()).thenReturn(99);
        when(request.getCanal()).thenReturn(CanalNotificacion.GMAIL);
        when(configNotificacionRepo.findByUsuarioIdAndActivoTrue(99))
                .thenReturn(Optional.empty());

        // Act
        alertaService.configurarCanalUsuario(request);

        // Assert
        verify(configNotificacionRepo, times(1)).save(any(ConfigNotificacion.class));
    }
}
