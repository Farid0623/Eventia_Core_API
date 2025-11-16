package cue.edu.co.eventia_core_api.domain.repository;

import cue.edu.co.eventia_core_api.domain.model.Asistencia;
import cue.edu.co.eventia_core_api.domain.model.EstadoAsistencia;

import java.util.List;
import java.util.Optional;

/**
 * Puerto (interfaz) para el repositorio de Asistencias
 */
public interface AsistenciaRepository {

    Asistencia save(Asistencia asistencia);

    Optional<Asistencia> findById(Long id);

    List<Asistencia> findByEventoId(Long eventoId);

    List<Asistencia> findByParticipanteId(Long participanteId);

    Optional<Asistencia> findByEventoIdAndParticipanteId(Long eventoId, Long participanteId);

    List<Asistencia> findByEventoIdAndEstado(Long eventoId, EstadoAsistencia estado);

    long countByEventoId(Long eventoId);

    long countByEventoIdAndEstado(Long eventoId, EstadoAsistencia estado);

    boolean existsByEventoIdAndParticipanteId(Long eventoId, Long participanteId);

    void deleteById(Long id);

    void deleteByEventoIdAndParticipanteId(Long eventoId, Long participanteId);
}

