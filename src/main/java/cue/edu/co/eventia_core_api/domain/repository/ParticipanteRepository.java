package cue.edu.co.eventia_core_api.domain.repository;

import cue.edu.co.eventia_core_api.domain.model.Participante;

import java.util.List;
import java.util.Optional;

/**
 * Puerto (interfaz) para el repositorio de Participantes
 */
public interface ParticipanteRepository {

    Participante save(Participante participante);

    Optional<Participante> findById(Long id);

    Optional<Participante> findByEmail(String email);

    Optional<Participante> findByDocumento(String documento);

    List<Participante> findAll();

    void deleteById(Long id);

    boolean existsById(Long id);

    boolean existsByEmail(String email);

    boolean existsByDocumento(String documento);

    long count();
}

