package cue.edu.co.eventia_core_api.infrastructure.persistence.mapper;

import cue.edu.co.eventia_core_api.domain.model.Participante;
import cue.edu.co.eventia_core_api.infrastructure.persistence.entity.ParticipanteEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper entre Participante (domain) y ParticipanteEntity (JPA)
 */
@Mapper(componentModel = "spring")
public interface ParticipanteEntityMapper {

    @Mapping(target = "asistencias", ignore = true)
    ParticipanteEntity toEntity(Participante participante);

    @Mapping(target = "asistencias", ignore = true)
    Participante toDomain(ParticipanteEntity entity);
}

