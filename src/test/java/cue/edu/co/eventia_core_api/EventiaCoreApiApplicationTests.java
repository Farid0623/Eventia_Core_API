package cue.edu.co.eventia_core_api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Prueba básica de carga de contexto de Spring Boot
 * Usa perfil 'test' con H2 en memoria (no requiere PostgreSQL)
 */
@SpringBootTest
@ActiveProfiles("test")
class EventiaCoreApiApplicationTests {

    @Test
    void contextLoads() {
        // Esta prueba verifica que el contexto de Spring se carga correctamente
    }

}
