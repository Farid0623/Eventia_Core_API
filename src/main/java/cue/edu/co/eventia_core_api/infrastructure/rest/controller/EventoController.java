package cue.edu.co.eventia_core_api.infrastructure.rest.controller;

import cue.edu.co.eventia_core_api.application.dto.CrearEventoRequest;
import cue.edu.co.eventia_core_api.application.dto.EventoResponse;
import cue.edu.co.eventia_core_api.application.mapper.EventoMapper;
import cue.edu.co.eventia_core_api.domain.model.EstadoEvento;
import cue.edu.co.eventia_core_api.domain.service.EventoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de Eventos
 * Expone los endpoints HTTP para operaciones con eventos
 */
@RestController  // Indica que esta clase maneja peticiones HTTP REST
@RequestMapping("/api/v1/eventos")  // Todas las rutas empiezan con /api/v1/eventos
@RequiredArgsConstructor  // Constructor automático para inyección de dependencias
@Slf4j  // Logger para registrar operaciones
public class EventoController {

    // Servicio que contiene la lógica de negocio
    private final EventoService eventoService;
    // Mapper para convertir entre objetos de dominio y DTOs
    private final EventoMapper eventoMapper;

    /**
     * Endpoint: POST /api/v1/eventos
     * Crea un nuevo evento
     * @param request Datos del evento a crear
     * @return 201 Created con el evento creado
     */
    @PostMapping
    public ResponseEntity<EventoResponse> crearEvento(@Valid @RequestBody CrearEventoRequest request) {
        log.info("POST /api/v1/eventos - Creando evento: {}", request.getNombre());

        // 1. Convertir DTO a objeto de dominio
        var evento = eventoMapper.toDomain(request);
        // 2. Llamar al servicio para crear el evento
        var eventoCreado = eventoService.crearEvento(evento);
        // 3. Convertir resultado a DTO y retornar con código 201 (Created)
        return ResponseEntity.status(HttpStatus.CREATED).body(eventoMapper.toResponse(eventoCreado));
    }

    /**
     * Endpoint: GET /api/v1/eventos
     * Obtiene la lista de todos los eventos
     * @return 200 OK con lista de eventos
     */
    @GetMapping
    public ResponseEntity<List<EventoResponse>> obtenerTodosLosEventos() {
        log.info("GET /api/v1/eventos - Obteniendo todos los eventos");

        // 1. Llamar al servicio para obtener todos los eventos
        var eventos = eventoService.obtenerTodosLosEventos();
        // 2. Convertir lista de eventos a DTOs y retornar
        return ResponseEntity.ok(eventoMapper.toResponseList(eventos));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventoResponse> obtenerEventoPorId(@PathVariable Long id) {
        log.info("GET /api/v1/eventos/{} - Obteniendo evento por ID", id);
        var evento = eventoService.obtenerEventoPorId(id);
        return ResponseEntity.ok(eventoMapper.toResponse(evento));
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<EventoResponse>> obtenerEventosDisponibles() {
        log.info("GET /api/v1/eventos/disponibles - Obteniendo eventos con capacidad");
        var eventos = eventoService.obtenerEventosConCapacidadDisponible();
        return ResponseEntity.ok(eventoMapper.toResponseList(eventos));
    }

    @GetMapping("/proximos")
    public ResponseEntity<List<EventoResponse>> obtenerEventosProximos() {
        log.info("GET /api/v1/eventos/proximos - Obteniendo eventos próximos");
        var eventos = eventoService.obtenerEventosProximos();
        return ResponseEntity.ok(eventoMapper.toResponseList(eventos));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<EventoResponse>> obtenerEventosPorEstado(@PathVariable EstadoEvento estado) {
        log.info("GET /api/v1/eventos/estado/{} - Obteniendo eventos por estado", estado);
        var eventos = eventoService.obtenerEventosPorEstado(estado);
        return ResponseEntity.ok(eventoMapper.toResponseList(eventos));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventoResponse> actualizarEvento(
            @PathVariable Long id,
            @Valid @RequestBody CrearEventoRequest request) {
        log.info("PUT /api/v1/eventos/{} - Actualizando evento", id);
        var evento = eventoMapper.toDomain(request);
        var eventoActualizado = eventoService.actualizarEvento(id, evento);
        return ResponseEntity.ok(eventoMapper.toResponse(eventoActualizado));
    }

    @PatchMapping("/{id}/estado/{nuevoEstado}")
    public ResponseEntity<EventoResponse> cambiarEstadoEvento(
            @PathVariable Long id,
            @PathVariable EstadoEvento nuevoEstado) {
        log.info("PATCH /api/v1/eventos/{}/estado/{} - Cambiando estado", id, nuevoEstado);
        var evento = eventoService.cambiarEstadoEvento(id, nuevoEstado);
        return ResponseEntity.ok(eventoMapper.toResponse(evento));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarEvento(@PathVariable Long id) {
        log.info("DELETE /api/v1/eventos/{} - Eliminando evento", id);
        eventoService.eliminarEvento(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/capacidad-disponible")
    public ResponseEntity<Boolean> verificarCapacidadDisponible(@PathVariable Long id) {
        log.info("GET /api/v1/eventos/{}/capacidad-disponible - Verificando capacidad", id);
        boolean disponible = eventoService.verificarCapacidadDisponible(id);
        return ResponseEntity.ok(disponible);
    }
}

