package cue.edu.co.eventia_core_api.application.mapper;

import cue.edu.co.eventia_core_api.application.dto.CrearEventoRequest;
import cue.edu.co.eventia_core_api.application.dto.EventoResponse;
import cue.edu.co.eventia_core_api.domain.model.Evento;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EventoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "participantesRegistrados", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    @Mapping(target = "asistencias", ignore = true)
    Evento toDomain(CrearEventoRequest request);

    @Mapping(target = "cuposDisponibles", expression = "java(evento.getCuposDisponibles())")
    @Mapping(target = "porcentajeOcupacion", expression = "java(evento.getPorcentajeOcupacion())")
    EventoResponse toResponse(Evento evento);

    List<EventoResponse> toResponseList(List<Evento> eventos);
}

