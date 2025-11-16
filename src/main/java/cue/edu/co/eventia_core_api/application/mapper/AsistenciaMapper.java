package cue.edu.co.eventia_core_api.application.mapper;

import cue.edu.co.eventia_core_api.application.dto.AsistenciaResponse;
import cue.edu.co.eventia_core_api.domain.model.Asistencia;
import cue.edu.co.eventia_core_api.domain.service.EventoService;
import cue.edu.co.eventia_core_api.domain.service.ParticipanteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AsistenciaMapper {

    @Autowired
    private EventoService eventoService;

    @Autowired
    private ParticipanteService participanteService;

    public AsistenciaResponse toResponse(Asistencia asistencia) {
        var evento = eventoService.obtenerEventoPorId(asistencia.getEventoId());
        var participante = participanteService.obtenerParticipantePorId(asistencia.getParticipanteId());

        return AsistenciaResponse.builder()
                .id(asistencia.getId())
                .eventoId(asistencia.getEventoId())
                .nombreEvento(evento.getNombre())
                .participanteId(asistencia.getParticipanteId())
                .nombreParticipante(participante.getNombreCompleto())
                .fechaRegistro(asistencia.getFechaRegistro())
                .estado(asistencia.getEstado())
                .notas(asistencia.getNotas())
                .fechaActualizacion(asistencia.getFechaActualizacion())
                .build();
    }

    public List<AsistenciaResponse> toResponseList(List<Asistencia> asistencias) {
        return asistencias.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}

