package com.nexoohub.almacen.alertas.service;

import com.nexoohub.almacen.alertas.dto.AlertaResponse;
import com.nexoohub.almacen.alertas.dto.ConfigNotificacionRequest;
import com.nexoohub.almacen.alertas.dto.ConfigurarAlertaRequest;
import com.nexoohub.almacen.alertas.entity.*;
import com.nexoohub.almacen.alertas.repository.AlertaSistemaRepository;
import com.nexoohub.almacen.alertas.repository.ConfigNotificacionRepository;
import com.nexoohub.almacen.alertas.repository.ConfiguracionAlertaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio principal del Motor de Alertas y Notificaciones (PRO-01).
 *
 * <p>Responsabilidades:
 * <ul>
 *   <li>Crear y persistir alertas del sistema.</li>
 *   <li>Despachar notificaciones según la configuración de canal del usuario destinatario.</li>
 *   <li>Gestionar el estado de las alertas (leída / resuelta).</li>
 *   <li>Configurar umbrales por sucursal y canales por usuario.</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AlertaService {

    private final AlertaSistemaRepository alertaRepo;
    private final ConfiguracionAlertaRepository configAlertaRepo;
    private final ConfigNotificacionRepository configNotifRepo;

    // Servicios de notificación — el de Telegram puede ser null si no está habilitado
    private final GmailNotificacionService gmailService;

    @Autowired(required = false)
    private TelegramNotificacionService telegramService;

    @Value("${spring.mail.from:noreply@nexoohub.com}")
    private String defaultEmail;

    // ─────────────────────────────── CRUD DE ALERTAS ─────────────────────────

    /**
     * Crea y persiste una alerta, evitando duplicados activos del mismo tipo/sucursal.
     *
     * @return la alerta creada (o null si ya existía una activa igual)
     */
    @Transactional
    public AlertaSistema crearAlerta(TipoAlerta tipo, String mensaje,
                                     Integer sucursalId, Integer usuarioDestinoId) {

        // Evitar duplicados activos
        if (alertaRepo.existsBySucursalIdAndTipoAndResueltaFalse(sucursalId, tipo)) {
            log.debug("Alerta tipo '{}' ya existe activa para sucursal {}. Se omite.", tipo, sucursalId);
            return null;
        }

        AlertaSistema alerta = new AlertaSistema(tipo, mensaje, sucursalId, usuarioDestinoId);
        AlertaSistema saved = alertaRepo.save(alerta);

        // Despachar la notificación al canal del usuario
        despacharNotificacion(saved);

        log.info("Alerta creada: tipo={}, sucursal={}, id={}", tipo, sucursalId, saved.getId());
        return saved;
    }

    /**
     * Lista las alertas no leídas de un usuario.
     */
    @Transactional(readOnly = true)
    public List<AlertaResponse> listarNoLeidas(Integer usuarioId) {
        return alertaRepo.findByUsuarioDestinoIdAndLeidaFalse(usuarioId)
                .stream()
                .map(AlertaResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * Lista las alertas no resueltas de una sucursal.
     */
    @Transactional(readOnly = true)
    public List<AlertaResponse> listarNoResueltasPorSucursal(Integer sucursalId) {
        return alertaRepo.findBySucursalIdAndResueltaFalse(sucursalId)
                .stream()
                .map(AlertaResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * Marca una alerta como leída.
     */
    @Transactional
    public void marcarLeida(Integer alertaId) {
        alertaRepo.findById(alertaId).ifPresent(a -> {
            a.setLeida(true);
            alertaRepo.save(a);
            log.info("Alerta {} marcada como leída.", alertaId);
        });
    }

    /**
     * Marca una alerta como resuelta (cierre definitivo).
     */
    @Transactional
    public void marcarResuelta(Integer alertaId) {
        alertaRepo.findById(alertaId).ifPresent(a -> {
            a.setResuelta(true);
            a.setLeida(true);
            alertaRepo.save(a);
            log.info("Alerta {} marcada como resuelta.", alertaId);
        });
    }

    /**
     * Cuenta las alertas no leídas de un usuario (para badge en UI).
     */
    @Transactional(readOnly = true)
    public long contarNoLeidas(Integer usuarioId) {
        return alertaRepo.countByUsuarioDestinoIdAndLeidaFalse(usuarioId);
    }

    // ──────────────────────── CONFIGURACIÓN DE UMBRALES ─────────────────────

    /**
     * Crea o actualiza la configuración de alertas de una sucursal.
     */
    @Transactional
    public void configurarSucursal(ConfigurarAlertaRequest req) {
        ConfiguracionAlerta config = configAlertaRepo
                .findBySucursalIdAndActivoTrue(req.getSucursalId())
                .orElse(new ConfiguracionAlerta());

        config.setSucursalId(req.getSucursalId());
        config.setStockMinimo(req.getStockMinimo());
        config.setDiasVencimientoCxC(req.getDiasVencimientoCxC());
        config.setPorcentajeMetaAlerta(req.getPorcentajeMetaAlerta());
        config.setActivo(true);

        configAlertaRepo.save(config);
        log.info("Configuración de alertas actualizada para sucursal {}", req.getSucursalId());
    }

    // ──────────────────────── CONFIGURACIÓN DE CANALES ─────────────────────

    /**
     * Registra o actualiza el canal de notificación preferido de un usuario.
     */
    @Transactional
    public void configurarCanalUsuario(ConfigNotificacionRequest req) {
        ConfigNotificacion config = configNotifRepo
                .findByUsuarioIdAndActivoTrue(req.getUsuarioId())
                .orElse(new ConfigNotificacion());

        config.setUsuarioId(req.getUsuarioId());
        config.setCanal(req.getCanal());
        config.setEmailDestino(req.getEmailDestino());
        config.setTelegramChatId(req.getTelegramChatId());
        config.setActivo(true);

        configNotifRepo.save(config);
        log.info("Canal de notificación configurado para usuario {}: {}", req.getUsuarioId(), req.getCanal());
    }

    // ──────────────────────── DESPACHO DE NOTIFICACIONES ─────────────────────

    /**
     * Envía la notificación al canal configurado del usuario destinatario.
     */
    void despacharNotificacion(AlertaSistema alerta) {
        if (alerta.getUsuarioDestinoId() == null) {
            log.debug("Alerta {} sin usuario destino; no se envía notificación.", alerta.getId());
            return;
        }

        Optional<ConfigNotificacion> configOpt =
                configNotifRepo.findByUsuarioIdAndActivoTrue(alerta.getUsuarioDestinoId());

        if (configOpt.isEmpty()) {
            log.debug("Usuario {} sin configuración de notificación registrada.", alerta.getUsuarioDestinoId());
            return;
        }

        ConfigNotificacion config = configOpt.get();
        String asunto = "Alerta: " + alerta.getTipo().name();
        String cuerpo = alerta.getMensaje();

        switch (config.getCanal()) {
            case TELEGRAM -> {
                if (telegramService != null && config.getTelegramChatId() != null) {
                    telegramService.enviar(config.getTelegramChatId(), cuerpo);
                } else {
                    log.warn("Telegram no disponible o chat_id no configurado para usuario {}.",
                            alerta.getUsuarioDestinoId());
                }
            }
            case AMBOS -> {
                if (config.getEmailDestino() != null) {
                    gmailService.enviar(config.getEmailDestino(), asunto, cuerpo);
                }
                if (telegramService != null && config.getTelegramChatId() != null) {
                    telegramService.enviar(config.getTelegramChatId(), cuerpo);
                }
            }
            default -> { // GMAIL
                String email = (config.getEmailDestino() != null)
                        ? config.getEmailDestino()
                        : defaultEmail;
                gmailService.enviar(email, asunto, cuerpo);
            }
        }
    }
}
