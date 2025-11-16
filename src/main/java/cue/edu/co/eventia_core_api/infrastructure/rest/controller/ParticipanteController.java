package cue.edu.co.eventia_core_api.infrastructure.rest.controller;

import cue.edu.co.eventia_core_api.application.dto.CrearParticipanteRequest;
import cue.edu.co.eventia_core_api.application.dto.ParticipanteResponse;
import cue.edu.co.eventia_core_api.application.mapper.ParticipanteMapper;
import cue.edu.co.eventia_core_api.domain.service.ParticipanteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de Participantes
 */
@RestController
@RequestMapping("/api/v1/participantes")
@RequiredArgsConstructor
@Slf4j
public class ParticipanteController {

    private final ParticipanteService participanteService;
    private final ParticipanteMapper participanteMapper;

    @PostMapping
    public ResponseEntity<ParticipanteResponse> crearParticipante(
            @Valid @RequestBody CrearParticipanteRequest request) {
        log.info("POST /api/v1/participantes - Creando participante: {}", request.getEmail());
        var participante = participanteMapper.toDomain(request);
        var participanteCreado = participanteService.crearParticipante(participante);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(participanteMapper.toResponse(participanteCreado));
    }

    @GetMapping
    public ResponseEntity<List<ParticipanteResponse>> obtenerTodosLosParticipantes() {
        log.info("GET /api/v1/participantes - Obteniendo todos los participantes");
        var participantes = participanteService.obtenerTodosLosParticipantes();
        return ResponseEntity.ok(participanteMapper.toResponseList(participantes));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParticipanteResponse> obtenerParticipantePorId(@PathVariable Long id) {
        log.info("GET /api/v1/participantes/{} - Obteniendo participante por ID", id);
        var participante = participanteService.obtenerParticipantePorId(id);
        return ResponseEntity.ok(participanteMapper.toResponse(participante));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ParticipanteResponse> obtenerParticipantePorEmail(@PathVariable String email) {
        log.info("GET /api/v1/participantes/email/{} - Obteniendo participante por email", email);
        var participante = participanteService.obtenerParticipantePorEmail(email);
        return ResponseEntity.ok(participanteMapper.toResponse(participante));
    }

    @GetMapping("/documento/{documento}")
    public ResponseEntity<ParticipanteResponse> obtenerParticipantePorDocumento(@PathVariable String documento) {
        log.info("GET /api/v1/participantes/documento/{} - Obteniendo participante por documento", documento);
        var participante = participanteService.obtenerParticipantePorDocumento(documento);
        return ResponseEntity.ok(participanteMapper.toResponse(participante));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ParticipanteResponse> actualizarParticipante(
            @PathVariable Long id,
            @Valid @RequestBody CrearParticipanteRequest request) {
        log.info("PUT /api/v1/participantes/{} - Actualizando participante", id);
        var participante = participanteMapper.toDomain(request);
        var participanteActualizado = participanteService.actualizarParticipante(id, participante);
        return ResponseEntity.ok(participanteMapper.toResponse(participanteActualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarParticipante(@PathVariable Long id) {
        log.info("DELETE /api/v1/participantes/{} - Eliminando participante", id);
        participanteService.eliminarParticipante(id);
        return ResponseEntity.noContent().build();
    }
}

