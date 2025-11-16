package cue.edu.co.eventia_core_api.application.mapper;

import cue.edu.co.eventia_core_api.application.dto.CrearParticipanteRequest;
import cue.edu.co.eventia_core_api.application.dto.ParticipanteResponse;
import cue.edu.co.eventia_core_api.domain.model.Participante;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ParticipanteMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    @Mapping(target = "asistencias", ignore = true)
    Participante toDomain(CrearParticipanteRequest request);

    @Mapping(target = "nombreCompleto", expression = "java(participante.getNombreCompleto())")
    ParticipanteResponse toResponse(Participante participante);

    List<ParticipanteResponse> toResponseList(List<Participante> participantes);
}

