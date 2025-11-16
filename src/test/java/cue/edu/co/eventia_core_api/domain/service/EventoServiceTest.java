package cue.edu.co.eventia_core_api.domain.service;

import cue.edu.co.eventia_core_api.domain.model.Evento;
import cue.edu.co.eventia_core_api.domain.model.EstadoEvento;
import cue.edu.co.eventia_core_api.domain.repository.EventoRepository;
import cue.edu.co.eventia_core_api.exception.BusinessRuleException;
import cue.edu.co.eventia_core_api.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EventoService - Pruebas Unitarias")
class EventoServiceTest {

    @Mock
    private EventoRepository eventoRepository;

    @InjectMocks
    private EventoService eventoService;

    private Evento eventoValido;

    @BeforeEach
    void setUp() {
        eventoValido = Evento.builder()
                .nombre("Conferencia Tech 2025")
                .descripcion("Evento de tecnología")
                .fechaInicio(LocalDateTime.now().plusDays(10))
                .fechaFin(LocalDateTime.now().plusDays(10).plusHours(8))
                .ubicacion("Centro de Convenciones")
                .capacidadMaxima(100)
                .participantesRegistrados(0)
                .estado(EstadoEvento.ACTIVO)
                .build();
    }

    @Test
    @DisplayName("Debe crear un evento exitosamente")
    void debeCrearEventoExitosamente() {
        // Given
        when(eventoRepository.save(any(Evento.class))).thenReturn(eventoValido);

        // When
        Evento resultado = eventoService.crearEvento(eventoValido);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombre()).isEqualTo("Conferencia Tech 2025");
        assertThat(resultado.getEstado()).isEqualTo(EstadoEvento.ACTIVO);
        verify(eventoRepository, times(1)).save(any(Evento.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando la fecha de inicio es en el pasado")
    void debeLanzarExcepcionCuandoFechaEsPasado() {
        // Given
        eventoValido.setFechaInicio(LocalDateTime.now().minusDays(1));

        // When & Then
        assertThatThrownBy(() -> eventoService.crearEvento(eventoValido))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("pasado");

        verify(eventoRepository, never()).save(any(Evento.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando la capacidad es cero o negativa")
    void debeLanzarExcepcionCuandoCapacidadInvalida() {
        // Given
        eventoValido.setCapacidadMaxima(0);

        // When & Then
        assertThatThrownBy(() -> eventoService.crearEvento(eventoValido))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("capacidad");

        verify(eventoRepository, never()).save(any(Evento.class));
    }

    @Test
    @DisplayName("Debe obtener evento por ID exitosamente")
    void debeObtenerEventoPorIdExitosamente() {
        // Given
        Long eventoId = 1L;
        eventoValido.setId(eventoId);
        when(eventoRepository.findById(eventoId)).thenReturn(Optional.of(eventoValido));

        // When
        Evento resultado = eventoService.obtenerEventoPorId(eventoId);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(eventoId);
        verify(eventoRepository, times(1)).findById(eventoId);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el evento no existe")
    void debeLanzarExcepcionCuandoEventoNoExiste() {
        // Given
        Long eventoId = 999L;
        when(eventoRepository.findById(eventoId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> eventoService.obtenerEventoPorId(eventoId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Evento");

        verify(eventoRepository, times(1)).findById(eventoId);
    }

    @Test
    @DisplayName("Debe obtener todos los eventos")
    void debeObtenerTodosLosEventos() {
        // Given
        List<Evento> eventos = Arrays.asList(eventoValido, eventoValido);
        when(eventoRepository.findAll()).thenReturn(eventos);

        // When
        List<Evento> resultado = eventoService.obtenerTodosLosEventos();

        // Then
        assertThat(resultado).isNotEmpty();
        assertThat(resultado).hasSize(2);
        verify(eventoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe eliminar evento exitosamente")
    void debeEliminarEventoExitosamente() {
        // Given
        Long eventoId = 1L;
        when(eventoRepository.existsById(eventoId)).thenReturn(true);
        doNothing().when(eventoRepository).deleteById(eventoId);

        // When
        eventoService.eliminarEvento(eventoId);

        // Then
        verify(eventoRepository, times(1)).existsById(eventoId);
        verify(eventoRepository, times(1)).deleteById(eventoId);
    }

    @Test
    @DisplayName("Debe verificar capacidad disponible correctamente")
    void debeVerificarCapacidadDisponible() {
        // Given
        Long eventoId = 1L;
        eventoValido.setId(eventoId);
        eventoValido.setCapacidadMaxima(100);
        eventoValido.setParticipantesRegistrados(50);
        when(eventoRepository.findById(eventoId)).thenReturn(Optional.of(eventoValido));

        // When
        boolean resultado = eventoService.verificarCapacidadDisponible(eventoId);

        // Then
        assertThat(resultado).isTrue();
        verify(eventoRepository, times(1)).findById(eventoId);
    }
}

