package cue.edu.co.eventia_core_api.infrastructure.persistence.adapter;

import cue.edu.co.eventia_core_api.domain.model.Evento;
import cue.edu.co.eventia_core_api.domain.model.EstadoEvento;
import cue.edu.co.eventia_core_api.domain.repository.EventoRepository;
import cue.edu.co.eventia_core_api.infrastructure.persistence.entity.EventoEntity;
import cue.edu.co.eventia_core_api.infrastructure.persistence.mapper.EventoEntityMapper;
import cue.edu.co.eventia_core_api.infrastructure.persistence.repository.JpaEventoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adaptador que implementa el puerto EventoRepository usando JPA
 */
@Component
@RequiredArgsConstructor
public class EventoRepositoryAdapter implements EventoRepository {

    private final JpaEventoRepository jpaEventoRepository;
    private final EventoEntityMapper mapper;

    @Override
    public Evento save(Evento evento) {
        EventoEntity entity = mapper.toEntity(evento);
        EventoEntity savedEntity = jpaEventoRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Evento> findById(Long id) {
        return jpaEventoRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<Evento> findAll() {
        return jpaEventoRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Evento> findByEstado(EstadoEvento estado) {
        return jpaEventoRepository.findByEstado(estado).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Evento> findEventosProximos(LocalDateTime desde) {
        return jpaEventoRepository.findEventosProximos(desde).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Evento> findEventosConCapacidadDisponible() {
        return jpaEventoRepository.findEventosConCapacidadDisponible().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        jpaEventoRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaEventoRepository.existsById(id);
    }

    @Override
    public long count() {
        return jpaEventoRepository.count();
    }
}

