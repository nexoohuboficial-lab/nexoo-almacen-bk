package com.nexoohub.almacen.common.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuración de Redis y Cache para NexooHub Almacén.
 * 
 * IMPORTANTE: Esta configuración solo se activa cuando:
 * - spring.cache.type=redis (en application-prod.yml)
 * 
 * En desarrollo (spring.cache.type=none), esta configuración NO se carga.
 * La aplicación funciona normalmente sin Redis, solo más lento.
 * 
 * Phase 3.3: Performance Optimization - Redis Caching
 */
@Configuration
@EnableCaching
@ConditionalOnProperty(name = "spring.cache.type", havingValue = "redis")
public class RedisConfig {

    /**
     * Configura el CacheManager de Redis con TTL personalizados por cache.
     * 
     * Estrategia de TTL:
     * - Catálogos estáticos (Categorias, TipoCliente): 1-2 horas
     * - Configuración financiera: 24 horas (cambia raramente)
     * - Precios especiales: 30 minutos (actualizaciones periódicas)
     * - Proveedores: 1 hora
     */
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        // Configuración por defecto (1 hora)
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(1))
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer())
                )
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(createJsonSerializer())
                )
                .disableCachingNullValues(); // No cachear valores null

        // Configuraciones específicas por cache con TTL optimizado
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        
        // Catálogos - TTL largo (cambian poco, se consultan mucho)
        cacheConfigurations.put("categorias", defaultConfig.entryTtl(Duration.ofHours(2)));
        cacheConfigurations.put("tiposCliente", defaultConfig.entryTtl(Duration.ofHours(2)));
        cacheConfigurations.put("proveedores", defaultConfig.entryTtl(Duration.ofHours(1)));
        
        // Configuración financiera - TTL muy largo (solo cambia con actualizaciones legales)
        cacheConfigurations.put("configuracionFinanciera", defaultConfig.entryTtl(Duration.ofHours(24)));
        
        // Precios especiales - TTL corto (actualizaciones frecuentes)
        cacheConfigurations.put("preciosEspeciales", defaultConfig.entryTtl(Duration.ofMinutes(30)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }

    /**
     * Crea un serializador JSON que soporta tipos complejos de JPA.
     * Incluye soporte para:
     * - LocalDateTime, LocalDate (java.time)
     * - Entidades JPA con relaciones lazy
     * - Colecciones de Spring Data (Page<T>)
     */
    private GenericJackson2JsonRedisSerializer createJsonSerializer() {
        ObjectMapper objectMapper = new ObjectMapper();
        
        // Soporte para java.time (LocalDateTime, LocalDate, etc.)
        objectMapper.registerModule(new JavaTimeModule());
        
        // Activar tipado por defecto para deserialización correcta
        objectMapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );
        
        return new GenericJackson2JsonRedisSerializer(objectMapper);
    }
}
