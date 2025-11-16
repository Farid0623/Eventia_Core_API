package cue.edu.co.eventia_core_api.infrastructure.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de caché para la aplicación.
 *
 * Nota:
 * - Dejamos que Spring Boot autoconfigure Redis (LettuceConnectionFactory, CacheManager, RedisTemplate, serializers, etc.).
 * - Los TTL y demás opciones básicas se controlan desde application.yml (spring.cache.redis.* y spring.data.redis.*).
 * - Esto elimina errores de IDE por resoluciones de clases y mantiene el comportamiento esperado.
 */
@Configuration
@EnableCaching
public class RedisConfig {

}
