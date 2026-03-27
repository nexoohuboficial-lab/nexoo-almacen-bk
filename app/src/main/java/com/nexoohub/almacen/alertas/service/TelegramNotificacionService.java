package com.nexoohub.almacen.alertas.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Servicio para enviar notificaciones a través de Telegram Bot API.
 * Solo se activa cuando la propiedad {@code alertas.telegram.enabled=true}.
 */
@Service
@ConditionalOnProperty(name = "alertas.telegram.enabled", havingValue = "true", matchIfMissing = false)
@Slf4j
public class TelegramNotificacionService {

    private static final String TELEGRAM_API_BASE = "https://api.telegram.org/bot";

    @Value("${alertas.telegram.bot-token}")
    private String botToken;

    private final RestTemplate restTemplate;

    public TelegramNotificacionService() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Envía un mensaje de texto a un chat de Telegram.
     *
     * @param chatId  ID del chat (usuario o grupo)
     * @param mensaje texto del mensaje a enviar
     */
    public void enviar(String chatId, String mensaje) {
        try {
            String mensajeCompleto = "🔔 *NexooHub ERP*\n\n" + mensaje;
            String encodedMsg = URLEncoder.encode(mensajeCompleto, StandardCharsets.UTF_8);
            String url = TELEGRAM_API_BASE + botToken
                    + "/sendMessage?chat_id=" + chatId
                    + "&text=" + encodedMsg
                    + "&parse_mode=Markdown";

            restTemplate.getForObject(url, String.class);
            log.info("Mensaje Telegram enviado al chat '{}'", chatId);

        } catch (Exception e) {
            log.error("Error al enviar mensaje Telegram al chat '{}': {}", chatId, e.getMessage(), e);
            // No propagamos para no interrumpir el flujo principal
        }
    }
}
