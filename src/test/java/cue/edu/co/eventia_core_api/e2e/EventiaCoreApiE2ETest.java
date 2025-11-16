package cue.edu.co.eventia_core_api.e2e;

import cue.edu.co.eventia_core_api.application.dto.CrearEventoRequest;
import cue.edu.co.eventia_core_api.application.dto.CrearParticipanteRequest;
import cue.edu.co.eventia_core_api.application.dto.RegistrarAsistenciaRequest;
import cue.edu.co.eventia_core_api.domain.model.TipoDocumento;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Eventia Core API - Pruebas End-to-End")
class EventiaCoreApiE2ETest {

    @LocalServerPort
    private int port;

    private static Long eventoId;
    private static Long participanteId;
    private static Long asistenciaId;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "/api/v1";
    }

    @Test
    @Order(1)
    @DisplayName("E2E: Crear un evento")
    void e2e_01_crearEvento() {
        CrearEventoRequest request = CrearEventoRequest.builder()
                .nombre("Workshop de Spring Boot E2E")
                .descripcion("Evento de prueba end-to-end")
                .fechaInicio(LocalDateTime.now().plusDays(30))
                .fechaFin(LocalDateTime.now().plusDays(30).plusHours(5))
                .ubicacion("Universidad del Cauca")
                .capacidadMaxima(30)
                .build();

        eventoId = given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/eventos")
                .then()
                .statusCode(201)
                .body("nombre", equalTo("Workshop de Spring Boot E2E"))
                .body("capacidadMaxima", equalTo(30))
                .body("participantesRegistrados", equalTo(0))
                .body("estado", equalTo("ACTIVO"))
                .extract()
                .path("id");

        Assertions.assertNotNull(eventoId);
    }

    @Test
    @Order(2)
    @DisplayName("E2E: Crear un participante")
    void e2e_02_crearParticipante() {
        CrearParticipanteRequest request = CrearParticipanteRequest.builder()
                .nombre("Carlos")
                .apellido("Test")
                .email("carlos.test.e2e@example.com")
                .telefono("3001234567")
                .documento("1234567890")
                .tipoDocumento(TipoDocumento.CEDULA_CIUDADANIA)
                .build();

        participanteId = given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/participantes")
                .then()
                .statusCode(201)
                .body("nombre", equalTo("Carlos"))
                .body("apellido", equalTo("Test"))
                .body("email", equalTo("carlos.test.e2e@example.com"))
                .extract()
                .path("id");

        Assertions.assertNotNull(participanteId);
    }

    @Test
    @Order(3)
    @DisplayName("E2E: Registrar asistencia de participante en evento")
    void e2e_03_registrarAsistencia() {
        RegistrarAsistenciaRequest request = RegistrarAsistenciaRequest.builder()
                .eventoId(eventoId)
                .participanteId(participanteId)
                .notas("Registro de prueba E2E")
                .build();

        asistenciaId = given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/asistencias")
                .then()
                .statusCode(201)
                .body("eventoId", equalTo(eventoId.intValue()))
                .body("participanteId", equalTo(participanteId.intValue()))
                .body("estado", equalTo("CONFIRMADO"))
                .extract()
                .path("id");

        Assertions.assertNotNull(asistenciaId);
    }

    @Test
    @Order(4)
    @DisplayName("E2E: Verificar que el evento incrementó participantes")
    void e2e_04_verificarIncrementoParticipantes() {
        given()
                .when()
                .get("/eventos/" + eventoId)
                .then()
                .statusCode(200)
                .body("participantesRegistrados", equalTo(1))
                .body("cuposDisponibles", equalTo(29));
    }

    @Test
    @Order(5)
    @DisplayName("E2E: Obtener estadísticas del evento")
    void e2e_05_obtenerEstadisticas() {
        given()
                .when()
                .get("/asistencias/evento/" + eventoId + "/estadisticas")
                .then()
                .statusCode(200)
                .body("eventoId", equalTo(eventoId.intValue()))
                .body("totalRegistrados", equalTo(1))
                .body("confirmados", equalTo(1))
                .body("cuposDisponibles", equalTo(29));
    }

    @Test
    @Order(6)
    @DisplayName("E2E: Evitar doble registro (regla de negocio)")
    void e2e_06_evitarDobleRegistro() {
        RegistrarAsistenciaRequest request = RegistrarAsistenciaRequest.builder()
                .eventoId(eventoId)
                .participanteId(participanteId)
                .notas("Intento de doble registro")
                .build();

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/asistencias")
                .then()
                .statusCode(422) // Unprocessable Entity
                .body("error", equalTo("Business Rule Violation"))
                .body("message", containsString("ya está registrado"));
    }

    @Test
    @Order(7)
    @DisplayName("E2E: Cancelar asistencia")
    void e2e_07_cancelarAsistencia() {
        given()
                .when()
                .patch("/asistencias/" + asistenciaId + "/cancelar")
                .then()
                .statusCode(200)
                .body("estado", equalTo("CANCELADO"));
    }

    @Test
    @Order(8)
    @DisplayName("E2E: Verificar que se decrementó el contador de participantes")
    void e2e_08_verificarDecrementoParticipantes() {
        given()
                .when()
                .get("/eventos/" + eventoId)
                .then()
                .statusCode(200)
                .body("participantesRegistrados", equalTo(0))
                .body("cuposDisponibles", equalTo(30));
    }
}

