package com.nexoohub.almacen.common.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.UUID;

@Component
public class LogFilter implements Filter {
    private static final Logger log = LoggerFactory.getLogger(LogFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        long startTime = System.currentTimeMillis();
        // Generamos un ID corto de 8 caracteres para el rastreo
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);

        try {
            HttpServletRequest req = (HttpServletRequest) request;
            log.info(">> Iniciando petición: {} {}", req.getMethod(), req.getRequestURI());
            
            chain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            log.info("<< Petición finalizada en {} ms", duration);
            
            // Si tarda más de 500ms, lanzamos una alerta en el log
            if (duration > 500) {
                log.warn("ALERTA DE RENDIMIENTO: La petición tardó más de lo esperado ({} ms)", duration);
            }
            MDC.remove("traceId");
        }
    }
}