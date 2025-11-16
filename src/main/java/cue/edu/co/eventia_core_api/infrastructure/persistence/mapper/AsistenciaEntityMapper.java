package cue.edu.co.eventia_core_api.infrastructure.persistence.mapper;

import cue.edu.co.eventia_core_api.domain.model.Asistencia;
import cue.edu.co.eventia_core_api.infrastructure.persistence.entity.AsistenciaEntity;
import cue.edu.co.eventia_core_api.infrastructure.persistence.entity.EventoEntity;
import cue.edu.co.eventia_core_api.infrastructure.persistence.entity.ParticipanteEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Mapper entre Asistencia (domain) y AsistenciaEntity (JPA)
 */
@Mapper(componentModel = "spring")
public abstract class AsistenciaEntityMapper {

    @Autowired
    protected JpaEventoRepositoryHelper eventoHelper;

    @Autowired
    protected JpaParticipanteRepositoryHelper participanteHelper;

    @Mapping(target = "evento", expression = "java(eventoHelper.getReferenceById(asistencia.getEventoId()))")
    @Mapping(target = "participante", expression = "java(participanteHelper.getReferenceById(asistencia.getParticipanteId()))")
    public abstract AsistenciaEntity toEntity(Asistencia asistencia);

    @Mapping(target = "eventoId", expression = "java(entity.getEvento().getId())")
    @Mapping(target = "participanteId", expression = "java(entity.getParticipante().getId())")
    public abstract Asistencia toDomain(AsistenciaEntity entity);

    @org.springframework.stereotype.Component
    public static class JpaEventoRepositoryHelper {
        @Autowired
        private cue.edu.co.eventia_core_api.infrastructure.persistence.repository.JpaEventoRepository repository;

        public EventoEntity getReferenceById(Long id) {
            return repository.getReferenceById(id);
        }
    }

    @org.springframework.stereotype.Component
    public static class JpaParticipanteRepositoryHelper {
        @Autowired
        private cue.edu.co.eventia_core_api.infrastructure.persistence.repository.JpaParticipanteRepository repository;

        public ParticipanteEntity getReferenceById(Long id) {
            return repository.getReferenceById(id);
        }
    }
}