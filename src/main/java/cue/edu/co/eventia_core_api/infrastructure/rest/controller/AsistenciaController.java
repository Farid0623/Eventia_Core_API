package cue.edu.co.eventia_core_api.infrastructure.rest.controller;

import cue.edu.co.eventia_core_api.application.dto.AsistenciaResponse;
import cue.edu.co.eventia_core_api.application.dto.RegistrarAsistenciaRequest;
import cue.edu.co.eventia_core_api.application.mapper.AsistenciaMapper;
import cue.edu.co.eventia_core_api.domain.service.AsistenciaService;
import cue.edu.co.eventia_core_api.domain.service.EstadisticasEvento;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de Asistencias
 */
@RestController
@RequestMapping("/api/v1/asistencias")
@RequiredArgsConstructor
@Slf4j
public class AsistenciaController {

    private final AsistenciaService asistenciaService;
    private final AsistenciaMapper asistenciaMapper;

    @PostMapping
    public ResponseEntity<AsistenciaResponse> registrarAsistencia(
            @Valid @RequestBody RegistrarAsistenciaRequest request) {
        log.info("POST /api/v1/asistencias - Registrando asistencia");
        var asistencia = asistenciaService.registrarAsistencia(
                request.getEventoId(),
                request.getParticipanteId(),
                request.getNotas()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(asistenciaMapper.toResponse(asistencia));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AsistenciaResponse> obtenerAsistenciaPorId(@PathVariable Long id) {
        log.info("GET /api/v1/asistencias/{} - Obteniendo asistencia por ID", id);
        var asistencia = asistenciaService.obtenerAsistenciaPorId(id);
        return ResponseEntity.ok(asistenciaMapper.toResponse(asistencia));
    }

    @GetMapping("/evento/{eventoId}")
    public ResponseEntity<List<AsistenciaResponse>> obtenerAsistenciasPorEvento(
            @PathVariable Long eventoId) {
        log.info("GET /api/v1/asistencias/evento/{} - Obteniendo asistencias por evento", eventoId);
        var asistencias = asistenciaService.obtenerAsistenciasPorEvento(eventoId);
        return ResponseEntity.ok(asistenciaMapper.toResponseList(asistencias));
    }

    @GetMapping("/participante/{participanteId}")
    public ResponseEntity<List<AsistenciaResponse>> obtenerAsistenciasPorParticipante(
            @PathVariable Long participanteId) {
        log.info("GET /api/v1/asistencias/participante/{} - Obteniendo asistencias por participante",
                participanteId);
        var asistencias = asistenciaService.obtenerAsistenciasPorParticipante(participanteId);
        return ResponseEntity.ok(asistenciaMapper.toResponseList(asistencias));
    }

    @GetMapping("/evento/{eventoId}/estadisticas")
    public ResponseEntity<EstadisticasEvento> obtenerEstadisticas(@PathVariable Long eventoId) {
        log.info("GET /api/v1/asistencias/evento/{}/estadisticas - Obteniendo estadísticas", eventoId);
        var estadisticas = asistenciaService.obtenerEstadisticas(eventoId);
        return ResponseEntity.ok(estadisticas);
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<AsistenciaResponse> cancelarAsistencia(@PathVariable Long id) {
        log.info("PATCH /api/v1/asistencias/{}/cancelar - Cancelando asistencia", id);
        var asistencia = asistenciaService.cancelarAsistencia(id);
        return ResponseEntity.ok(asistenciaMapper.toResponse(asistencia));
    }

    @PatchMapping("/{id}/marcar-asistio")
    public ResponseEntity<AsistenciaResponse> marcarAsistio(@PathVariable Long id) {
        log.info("PATCH /api/v1/asistencias/{}/marcar-asistio - Marcando asistió", id);
        var asistencia = asistenciaService.marcarAsistio(id);
        return ResponseEntity.ok(asistenciaMapper.toResponse(asistencia));
    }

    @PatchMapping("/{id}/marcar-no-asistio")
    public ResponseEntity<AsistenciaResponse> marcarNoAsistio(@PathVariable Long id) {
        log.info("PATCH /api/v1/asistencias/{}/marcar-no-asistio - Marcando NO asistió", id);
        var asistencia = asistenciaService.marcarNoAsistio(id);
        return ResponseEntity.ok(asistenciaMapper.toResponse(asistencia));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarAsistencia(@PathVariable Long id) {
        log.info("DELETE /api/v1/asistencias/{} - Eliminando asistencia", id);
        asistenciaService.eliminarAsistencia(id);
        return ResponseEntity.noContent().build();
    }
}

