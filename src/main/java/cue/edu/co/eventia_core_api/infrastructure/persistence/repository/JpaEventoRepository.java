package cue.edu.co.eventia_core_api.infrastructure.persistence.repository;

import cue.edu.co.eventia_core_api.domain.model.EstadoEvento;
import cue.edu.co.eventia_core_api.infrastructure.persistence.entity.EventoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio JPA para EventoEntity
 */
@Repository
public interface JpaEventoRepository extends JpaRepository<EventoEntity, Long> {

    List<EventoEntity> findByEstado(EstadoEvento estado);

    @Query("SELECT e FROM EventoEntity e WHERE e.fechaInicio >= :desde ORDER BY e.fechaInicio ASC")
    List<EventoEntity> findEventosProximos(@Param("desde") LocalDateTime desde);

    @Query("SELECT e FROM EventoEntity e WHERE e.participantesRegistrados < e.capacidadMaxima AND e.estado = 'ACTIVO'")
    List<EventoEntity> findEventosConCapacidadDisponible();
}

