package cue.edu.co.eventia_core_api.domain.repository;

import cue.edu.co.eventia_core_api.domain.model.Evento;
import cue.edu.co.eventia_core_api.domain.model.EstadoEvento;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Puerto (interfaz) para el repositorio de Eventos
 * Define las operaciones que deben ser implementadas por la capa de infraestructura
 */
public interface EventoRepository {

    Evento save(Evento evento);

    Optional<Evento> findById(Long id);

    List<Evento> findAll();

    List<Evento> findByEstado(EstadoEvento estado);

    List<Evento> findEventosProximos(LocalDateTime desde);

    List<Evento> findEventosConCapacidadDisponible();

    void deleteById(Long id);

    boolean existsById(Long id);

    long count();
}

