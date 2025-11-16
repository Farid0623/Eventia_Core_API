package cue.edu.co.eventia_core_api.infrastructure.persistence.repository;

import cue.edu.co.eventia_core_api.domain.model.EstadoAsistencia;
import cue.edu.co.eventia_core_api.infrastructure.persistence.entity.AsistenciaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para AsistenciaEntity
 */
@Repository
public interface JpaAsistenciaRepository extends JpaRepository<AsistenciaEntity, Long> {

    @Query("SELECT a FROM AsistenciaEntity a WHERE a.evento.id = :eventoId")
    List<AsistenciaEntity> findByEventoId(@Param("eventoId") Long eventoId);

    @Query("SELECT a FROM AsistenciaEntity a WHERE a.participante.id = :participanteId")
    List<AsistenciaEntity> findByParticipanteId(@Param("participanteId") Long participanteId);

    @Query("SELECT a FROM AsistenciaEntity a WHERE a.evento.id = :eventoId AND a.participante.id = :participanteId")
    Optional<AsistenciaEntity> findByEventoIdAndParticipanteId(
        @Param("eventoId") Long eventoId,
        @Param("participanteId") Long participanteId
    );

    @Query("SELECT a FROM AsistenciaEntity a WHERE a.evento.id = :eventoId AND a.estado = :estado")
    List<AsistenciaEntity> findByEventoIdAndEstado(
        @Param("eventoId") Long eventoId,
        @Param("estado") EstadoAsistencia estado
    );

    @Query("SELECT COUNT(a) FROM AsistenciaEntity a WHERE a.evento.id = :eventoId")
    long countByEventoId(@Param("eventoId") Long eventoId);

    @Query("SELECT COUNT(a) FROM AsistenciaEntity a WHERE a.evento.id = :eventoId AND a.estado = :estado")
    long countByEventoIdAndEstado(
        @Param("eventoId") Long eventoId,
        @Param("estado") EstadoAsistencia estado
    );

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM AsistenciaEntity a WHERE a.evento.id = :eventoId AND a.participante.id = :participanteId")
    boolean existsByEventoIdAndParticipanteId(
        @Param("eventoId") Long eventoId,
        @Param("participanteId") Long participanteId
    );

    @Query("DELETE FROM AsistenciaEntity a WHERE a.evento.id = :eventoId AND a.participante.id = :participanteId")
    void deleteByEventoIdAndParticipanteId(
        @Param("eventoId") Long eventoId,
        @Param("participanteId") Long participanteId
    );
}

