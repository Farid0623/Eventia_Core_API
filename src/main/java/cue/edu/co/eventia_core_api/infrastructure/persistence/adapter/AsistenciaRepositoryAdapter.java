package cue.edu.co.eventia_core_api.infrastructure.persistence.adapter;

import cue.edu.co.eventia_core_api.domain.model.Asistencia;
import cue.edu.co.eventia_core_api.domain.model.EstadoAsistencia;
import cue.edu.co.eventia_core_api.domain.repository.AsistenciaRepository;
import cue.edu.co.eventia_core_api.infrastructure.persistence.entity.AsistenciaEntity;
import cue.edu.co.eventia_core_api.infrastructure.persistence.mapper.AsistenciaEntityMapper;
import cue.edu.co.eventia_core_api.infrastructure.persistence.repository.JpaAsistenciaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adaptador que implementa el puerto AsistenciaRepository usando JPA
 */
@Component
@RequiredArgsConstructor
public class AsistenciaRepositoryAdapter implements AsistenciaRepository {

    private final JpaAsistenciaRepository jpaAsistenciaRepository;
    private final AsistenciaEntityMapper mapper;

    @Override
    @Transactional
    public Asistencia save(Asistencia asistencia) {
        AsistenciaEntity entity = mapper.toEntity(asistencia);
        AsistenciaEntity savedEntity = jpaAsistenciaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Asistencia> findById(Long id) {
        return jpaAsistenciaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<Asistencia> findByEventoId(Long eventoId) {
        return jpaAsistenciaRepository.findByEventoId(eventoId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Asistencia> findByParticipanteId(Long participanteId) {
        return jpaAsistenciaRepository.findByParticipanteId(participanteId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Asistencia> findByEventoIdAndParticipanteId(Long eventoId, Long participanteId) {
        return jpaAsistenciaRepository.findByEventoIdAndParticipanteId(eventoId, participanteId)
                .map(mapper::toDomain);
    }

    @Override
    public List<Asistencia> findByEventoIdAndEstado(Long eventoId, EstadoAsistencia estado) {
        return jpaAsistenciaRepository.findByEventoIdAndEstado(eventoId, estado).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long countByEventoId(Long eventoId) {
        return jpaAsistenciaRepository.countByEventoId(eventoId);
    }

    @Override
    public long countByEventoIdAndEstado(Long eventoId, EstadoAsistencia estado) {
        return jpaAsistenciaRepository.countByEventoIdAndEstado(eventoId, estado);
    }

    @Override
    public boolean existsByEventoIdAndParticipanteId(Long eventoId, Long participanteId) {
        return jpaAsistenciaRepository.existsByEventoIdAndParticipanteId(eventoId, participanteId);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        jpaAsistenciaRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteByEventoIdAndParticipanteId(Long eventoId, Long participanteId) {
        jpaAsistenciaRepository.deleteByEventoIdAndParticipanteId(eventoId, participanteId);
    }
}

