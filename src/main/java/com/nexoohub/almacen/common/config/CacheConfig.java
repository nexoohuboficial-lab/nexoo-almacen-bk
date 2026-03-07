package com.nexoohub.almacen.common.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración global de Cache con manejo de errores elegante.
 * 
 * COMPORTAMIENTO:
 * - Si Redis está disponible → Cache funcionando (mejora 90% performance)
 * - Si Redis NO está disponible → Aplicación sigue funcionando (sin cache)
 * - Si Redis falla DURANTE ejecución → Logs warning, continúa sin cache
 * 
 * Esta estrategia permite:
 * 1. Desarrollo sin Redis instalado
 * 2. Producción resiliente (si Redis cae, la app no crashea)
 * 3. Logs claros para debugging
 * 
 * Phase 3.3: Performance Optimization - Graceful Cache Degradation
 */
@Configuration
public class CacheConfig implements CachingConfigurer {

    private static final Logger log = LoggerFactory.getLogger(CacheConfig.class);

    /**
     * Manejador de errores custom para Redis.
     * Loggea el error pero NO lanza excepción → Graceful degradation
     */
    @Override
    public CacheErrorHandler errorHandler() {
        return new CacheErrorHandler() {
            
            @Override
            public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
                log.warn("⚠️ Redis GET error en cache '{}' con key '{}'. Continuando sin cache. Error: {}", 
                         cache.getName(), key, exception.getMessage());
                // NO lanzamos excepción → La aplicación busca en BD directamente
            }

            @Override
            public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
                log.warn("⚠️ Redis PUT error en cache '{}' con key '{}'. Valor no cacheado. Error: {}", 
                         cache.getName(), key, exception.getMessage());
                // NO lanzamos excepción → La operación continúa sin cachear
            }

            @Override
            public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
                log.warn("⚠️ Redis EVICT error en cache '{}' con key '{}'. Cache no limpiado. Error: {}", 
                         cache.getName(), key, exception.getMessage());
                // NO lanzamos excepción → El dato puede quedar "stale" en cache
            }

            @Override
            public void handleCacheClearError(RuntimeException exception, Cache cache) {
                log.warn("⚠️ Redis CLEAR error en cache '{}'. Cache no limpiado completamente. Error: {}", 
                         cache.getName(), exception.getMessage());
                // NO lanzamos excepción → Algunos datos pueden quedar en cache
            }
        };
    }
}
