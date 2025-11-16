package cue.edu.co.eventia_core_api.infrastructure.rest.controller;

import cue.edu.co.eventia_core_api.application.dto.CrearEventoRequest;
import cue.edu.co.eventia_core_api.domain.model.EstadoEvento;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("EventoController - Pruebas de Integración")
class EventoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Debe crear un evento y retornar 201 Created")
    void debeCrearEventoYRetornar201() throws Exception {
        // Given
        CrearEventoRequest request = CrearEventoRequest.builder()
                .nombre("Evento de Prueba")
                .descripcion("Descripción del evento")
                .fechaInicio(LocalDateTime.now().plusDays(5))
                .fechaFin(LocalDateTime.now().plusDays(5).plusHours(4))
                .ubicacion("Popayán, Colombia")
                .capacidadMaxima(50)
                .build();

        // When & Then
        mockMvc.perform(post("/api/v1/eventos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nombre").value("Evento de Prueba"))
                .andExpect(jsonPath("$.capacidadMaxima").value(50))
                .andExpect(jsonPath("$.participantesRegistrados").value(0))
                .andExpect(jsonPath("$.estado").value("ACTIVO"));
    }

    @Test
    @DisplayName("Debe retornar 400 Bad Request cuando los datos son inválidos")
    void debeRetornar400CuandoDatosInvalidos() throws Exception {
        // Given
        CrearEventoRequest request = CrearEventoRequest.builder()
                .nombre("") // Nombre vacío
                .capacidadMaxima(-1) // Capacidad negativa
                .build();

        // When & Then
        mockMvc.perform(post("/api/v1/eventos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Debe obtener todos los eventos")
    void debeObtenerTodosLosEventos() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/eventos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Debe retornar 404 cuando el evento no existe")
    void debeRetornar404CuandoEventoNoExiste() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/eventos/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"));
    }

    @Test
    @DisplayName("Debe obtener eventos con capacidad disponible")
    void debeObtenerEventosConCapacidad() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/eventos/disponibles"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}

