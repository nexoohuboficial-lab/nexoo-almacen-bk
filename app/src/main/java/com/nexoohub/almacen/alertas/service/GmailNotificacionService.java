package com.nexoohub.almacen.alertas.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Servicio para enviar notificaciones por correo electrónico (Gmail/SMTP).
 * Usa spring-boot-starter-mail con la configuración en application.yml.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GmailNotificacionService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.from:noreply@nexoohub.com}")
    private String fromEmail;

    /**
     * Envía un correo simple de texto plano.
     *
     * @param destinatario dirección de email del destinatario
     * @param asunto       asunto del correo
     * @param cuerpo       cuerpo del mensaje
     */
    public void enviar(String destinatario, String asunto, String cuerpo) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(destinatario);
            message.setSubject("[NexooHub] " + asunto);
            message.setText(cuerpo + "\n\n---\nEste mensaje fue generado automáticamente por NexooHub ERP.");

            mailSender.send(message);
            log.info("Correo enviado a '{}' con asunto '{}'", destinatario, asunto);

        } catch (Exception e) {
            log.error("Error al enviar correo a '{}': {}", destinatario, e.getMessage(), e);
            // No propagamos la excepción para que el scheduler no se detenga
        }
    }
}
