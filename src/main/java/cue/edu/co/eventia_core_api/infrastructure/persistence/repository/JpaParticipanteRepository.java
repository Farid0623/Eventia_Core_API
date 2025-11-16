package cue.edu.co.eventia_core_api.infrastructure.persistence.repository;

import cue.edu.co.eventia_core_api.infrastructure.persistence.entity.ParticipanteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio JPA para ParticipanteEntity
 */
@Repository
public interface JpaParticipanteRepository extends JpaRepository<ParticipanteEntity, Long> {

    Optional<ParticipanteEntity> findByEmail(String email);

    Optional<ParticipanteEntity> findByDocumento(String documento);

    boolean existsByEmail(String email);

    boolean existsByDocumento(String documento);
}

