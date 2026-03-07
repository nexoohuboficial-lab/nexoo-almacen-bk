package com.nexoohub.almacen.common.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de OpenAPI/Swagger para documentación de la API.
 * 
 * <p>SpringDoc se auto-configura y genera automáticamente la documentación en:</p>
 * <ul>
 *  <li>Swagger UI: /swagger-ui.html</li>
 *  <li>API Docs (JSON): /v3/api-docs</li>
 * </ul>
 * 
 * <p>La seguridad JWT se detecta automáticamente de SecurityConfig.</p>
 * 
 * <p>Se deshabilita automáticamente en el perfil 'test' para no interferir con los tests.</p>
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
@Configuration
@ConditionalOnProperty(name = "springdoc.swagger-ui.enabled", havingValue = "true", matchIfMissing = true)
public class OpenApiConfig {
    // SpringDoc auto-configura todo - no necesitamos configuración manual
}
