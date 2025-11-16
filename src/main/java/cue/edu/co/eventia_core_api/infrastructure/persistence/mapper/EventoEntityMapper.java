package cue.edu.co.eventia_core_api.infrastructure.persistence.mapper;

import cue.edu.co.eventia_core_api.domain.model.Evento;
import cue.edu.co.eventia_core_api.infrastructure.persistence.entity.EventoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper entre Evento (domain) y EventoEntity (JPA)
 */
@Mapper(componentModel = "spring")
public interface EventoEntityMapper {

    @Mapping(target = "asistencias", ignore = true)
    EventoEntity toEntity(Evento evento);

    @Mapping(target = "asistencias", ignore = true)
    Evento toDomain(EventoEntity entity);
}

