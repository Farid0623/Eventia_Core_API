package cue.edu.co.eventia_core_api.domain.service;

import cue.edu.co.eventia_core_api.domain.model.Evento;
import cue.edu.co.eventia_core_api.domain.model.EstadoEvento;
import cue.edu.co.eventia_core_api.domain.repository.EventoRepository;
import cue.edu.co.eventia_core_api.exception.BusinessRuleException;
import cue.edu.co.eventia_core_api.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio de dominio para la gestión de Eventos
 * Contiene la lógica de negocio relacionada con eventos
 */
@Service  // Indica que esta clase es un servicio de Spring
@RequiredArgsConstructor  // Lombok crea el constructor automáticamente
@Slf4j  // Lombok proporciona el logger (log)
public class EventoService {

    // Repositorio para acceder a la base de datos de eventos
    private final EventoRepository eventoRepository;

    /**
     * Crear un nuevo evento
     * - Valida los datos del evento
     * - Establece valores iniciales (participantes en 0, estado ACTIVO)
     * - Guarda en la base de datos
     * - Limpia el caché para que las consultas futuras vean el nuevo evento
     */
    @Transactional  // Si algo falla, todo se revierte (rollback)
    @CacheEvict(value = {"eventos", "eventosDisponibles", "eventosProximos"}, allEntries = true)  // Limpia el caché
    public Evento crearEvento(Evento evento) {
        log.info("Creando nuevo evento: {}", evento.getNombre());

        // Validar que los datos del evento sean correctos
        validarDatosEvento(evento);

        // Establecer valores iniciales
        evento.setParticipantesRegistrados(0);
        evento.setEstado(EstadoEvento.ACTIVO);
        evento.setFechaCreacion(LocalDateTime.now());

        // Guardar en la base de datos
        return eventoRepository.save(evento);
    }

    /**
     * Actualizar un evento existente
     * - Busca el evento por ID
     * - Valida los nuevos datos
     * - Verifica que la nueva capacidad no sea menor a los ya registrados
     * - Actualiza y guarda
     */
    @Transactional
    @CacheEvict(value = {"eventos", "eventosDisponibles", "eventosProximos"}, allEntries = true)
    public Evento actualizarEvento(Long id, Evento eventoActualizado) {
        log.info("Actualizando evento con ID: {}", id);

        // Buscar el evento actual en la base de datos
        Evento eventoExistente = obtenerEventoPorId(id);

        // Validar los nuevos datos
        validarDatosEvento(eventoActualizado);

        // Validar que la nueva capacidad no sea menor a los participantes ya registrados
        if (eventoActualizado.getCapacidadMaxima() < eventoExistente.getParticipantesRegistrados()) {
            throw new BusinessRuleException(
                "La nueva capacidad no puede ser menor al número de participantes ya registrados"
            );
        }

        eventoActualizado.setId(id);
        eventoActualizado.setParticipantesRegistrados(eventoExistente.getParticipantesRegistrados());
        eventoActualizado.setFechaCreacion(eventoExistente.getFechaCreacion());
        eventoActualizado.setFechaActualizacion(LocalDateTime.now());

        return eventoRepository.save(eventoActualizado);
    }

    @Cacheable(value = "eventos", key = "#id")
    public Evento obtenerEventoPorId(Long id) {
        log.debug("Obteniendo evento por ID: {}", id);
        return eventoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento", "id", id));
    }

    @Cacheable(value = "eventos")
    public List<Evento> obtenerTodosLosEventos() {
        log.debug("Obteniendo todos los eventos");
        return eventoRepository.findAll();
    }

    @Cacheable(value = "eventosDisponibles")
    public List<Evento> obtenerEventosConCapacidadDisponible() {
        log.debug("Obteniendo eventos con capacidad disponible");
        return eventoRepository.findEventosConCapacidadDisponible();
    }

    @Cacheable(value = "eventosProximos")
    public List<Evento> obtenerEventosProximos() {
        log.debug("Obteniendo eventos próximos");
        return eventoRepository.findEventosProximos(LocalDateTime.now());
    }

    public List<Evento> obtenerEventosPorEstado(EstadoEvento estado) {
        log.debug("Obteniendo eventos con estado: {}", estado);
        return eventoRepository.findByEstado(estado);
    }

    @Transactional
    @CacheEvict(value = {"eventos", "eventosDisponibles", "eventosProximos"}, allEntries = true)
    public void eliminarEvento(Long id) {
        log.info("Eliminando evento con ID: {}", id);

        if (!eventoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Evento", "id", id);
        }

        eventoRepository.deleteById(id);
    }

    @Transactional
    @CacheEvict(value = {"eventos", "eventosDisponibles", "eventosProximos"}, allEntries = true)
    public Evento cambiarEstadoEvento(Long id, EstadoEvento nuevoEstado) {
        log.info("Cambiando estado del evento {} a {}", id, nuevoEstado);

        Evento evento = obtenerEventoPorId(id);
        evento.setEstado(nuevoEstado);
        evento.setFechaActualizacion(LocalDateTime.now());

        return eventoRepository.save(evento);
    }

    /**
     * Verifica si un evento tiene capacidad disponible (con caché)
     */
    @Cacheable(value = "capacidadEvento", key = "#eventoId")
    public boolean verificarCapacidadDisponible(Long eventoId) {
        Evento evento = obtenerEventoPorId(eventoId);
        return evento.tieneCapacidadDisponible() && evento.estaActivo();
    }

    /**
     * Incrementa el contador de participantes registrados
     */
    @Transactional
    @CacheEvict(value = {"eventos", "eventosDisponibles", "eventosProximos", "capacidadEvento"}, allEntries = true)
    public void incrementarParticipantes(Long eventoId) {
        Evento evento = obtenerEventoPorId(eventoId);
        evento.setParticipantesRegistrados(evento.getParticipantesRegistrados() + 1);
        evento.setFechaActualizacion(LocalDateTime.now());
        eventoRepository.save(evento);
    }

    /**
     * Decrementa el contador de participantes registrados
     */
    @Transactional
    @CacheEvict(value = {"eventos", "eventosDisponibles", "eventosProximos", "capacidadEvento"}, allEntries = true)
    public void decrementarParticipantes(Long eventoId) {
        Evento evento = obtenerEventoPorId(eventoId);
        if (evento.getParticipantesRegistrados() > 0) {
            evento.setParticipantesRegistrados(evento.getParticipantesRegistrados() - 1);
            evento.setFechaActualizacion(LocalDateTime.now());
            eventoRepository.save(evento);
        }
    }

    private void validarDatosEvento(Evento evento) {
        if (evento.getFechaInicio().isBefore(LocalDateTime.now())) {
            throw new BusinessRuleException("La fecha de inicio no puede ser en el pasado");
        }

        if (evento.getFechaFin().isBefore(evento.getFechaInicio())) {
            throw new BusinessRuleException("La fecha de fin debe ser posterior a la fecha de inicio");
        }

        if (evento.getCapacidadMaxima() <= 0) {
            throw new BusinessRuleException("La capacidad máxima debe ser mayor a 0");
        }
    }
}

