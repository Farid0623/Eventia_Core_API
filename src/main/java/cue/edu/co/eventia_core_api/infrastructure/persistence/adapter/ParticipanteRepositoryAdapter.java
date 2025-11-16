package cue.edu.co.eventia_core_api.infrastructure.persistence.adapter;

import cue.edu.co.eventia_core_api.domain.model.Participante;
import cue.edu.co.eventia_core_api.domain.repository.ParticipanteRepository;
import cue.edu.co.eventia_core_api.infrastructure.persistence.entity.ParticipanteEntity;
import cue.edu.co.eventia_core_api.infrastructure.persistence.mapper.ParticipanteEntityMapper;
import cue.edu.co.eventia_core_api.infrastructure.persistence.repository.JpaParticipanteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adaptador que implementa el puerto ParticipanteRepository usando JPA
 */
@Component
@RequiredArgsConstructor
public class ParticipanteRepositoryAdapter implements ParticipanteRepository {

    private final JpaParticipanteRepository jpaParticipanteRepository;
    private final ParticipanteEntityMapper mapper;

    @Override
    public Participante save(Participante participante) {
        ParticipanteEntity entity = mapper.toEntity(participante);
        ParticipanteEntity savedEntity = jpaParticipanteRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Participante> findById(Long id) {
        return jpaParticipanteRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Participante> findByEmail(String email) {
        return jpaParticipanteRepository.findByEmail(email)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Participante> findByDocumento(String documento) {
        return jpaParticipanteRepository.findByDocumento(documento)
                .map(mapper::toDomain);
    }

    @Override
    public List<Participante> findAll() {
        return jpaParticipanteRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        jpaParticipanteRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaParticipanteRepository.existsById(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaParticipanteRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByDocumento(String documento) {
        return jpaParticipanteRepository.existsByDocumento(documento);
    }

    @Override
    public long count() {
        return jpaParticipanteRepository.count();
    }
}

