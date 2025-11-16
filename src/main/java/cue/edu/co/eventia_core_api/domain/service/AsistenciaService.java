package cue.edu.co.eventia_core_api.domain.service;

import cue.edu.co.eventia_core_api.domain.model.Asistencia;
import cue.edu.co.eventia_core_api.domain.model.EstadoAsistencia;
import cue.edu.co.eventia_core_api.domain.model.Evento;
import cue.edu.co.eventia_core_api.domain.repository.AsistenciaRepository;
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
 * Servicio de dominio para la gestión de Asistencias
 * Implementa reglas de negocio como validación de cupos y evitar doble registro
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AsistenciaService {

    private final AsistenciaRepository asistenciaRepository;
    private final EventoService eventoService;
    private final ParticipanteService participanteService;

    /**
     * Registra un participante en un evento
     * Valida capacidad y doble registro
     */
    @Transactional
    @CacheEvict(value = {"asistencias", "estadisticasEvento"}, allEntries = true)
    public Asistencia registrarAsistencia(Long eventoId, Long participanteId, String notas) {
        log.info("Registrando asistencia - Evento: {}, Participante: {}", eventoId, participanteId);

        // Validar que existan el evento y el participante
        Evento evento = eventoService.obtenerEventoPorId(eventoId);
        participanteService.obtenerParticipantePorId(participanteId);

        // Validar que el evento esté activo
        if (!evento.estaActivo()) {
            throw new BusinessRuleException("No se puede registrar en un evento que no está activo");
        }

        // Validar que el evento no haya finalizado
        if (evento.haFinalizado()) {
            throw new BusinessRuleException("No se puede registrar en un evento que ya finalizó");
        }

        // Validar que no exista un registro previo (evitar doble registro)
        if (asistenciaRepository.existsByEventoIdAndParticipanteId(eventoId, participanteId)) {
            throw new BusinessRuleException("El participante ya está registrado en este evento");
        }

        // Validar capacidad disponible
        if (!evento.tieneCapacidadDisponible()) {
            throw new BusinessRuleException("El evento ha alcanzado su capacidad máxima");
        }

        // Crear la asistencia
        Asistencia asistencia = Asistencia.builder()
                .eventoId(eventoId)
                .participanteId(participanteId)
                .fechaRegistro(LocalDateTime.now())
                .estado(EstadoAsistencia.CONFIRMADO)
                .notas(notas)
                .build();

        Asistencia asistenciaGuardada = asistenciaRepository.save(asistencia);

        // Incrementar contador de participantes en el evento
        eventoService.incrementarParticipantes(eventoId);

        log.info("Asistencia registrada exitosamente con ID: {}", asistenciaGuardada.getId());
        return asistenciaGuardada;
    }

    /**
     * Cancela una asistencia
     */
    @Transactional
    @CacheEvict(value = {"asistencias", "estadisticasEvento"}, allEntries = true)
    public Asistencia cancelarAsistencia(Long asistenciaId) {
        log.info("Cancelando asistencia con ID: {}", asistenciaId);

        Asistencia asistencia = obtenerAsistenciaPorId(asistenciaId);

        if (asistencia.getEstado() == EstadoAsistencia.CANCELADO) {
            throw new BusinessRuleException("La asistencia ya está cancelada");
        }

        asistencia.cancelar();
        Asistencia asistenciaActualizada = asistenciaRepository.save(asistencia);

        // Decrementar contador de participantes en el evento
        eventoService.decrementarParticipantes(asistencia.getEventoId());

        return asistenciaActualizada;
    }

    /**
     * Marca que un participante asistió al evento
     */
    @Transactional
    @CacheEvict(value = {"asistencias", "estadisticasEvento"}, allEntries = true)
    public Asistencia marcarAsistio(Long asistenciaId) {
        log.info("Marcando asistencia como asistió: {}", asistenciaId);

        Asistencia asistencia = obtenerAsistenciaPorId(asistenciaId);
        asistencia.marcarAsistio();

        return asistenciaRepository.save(asistencia);
    }

    /**
     * Marca que un participante NO asistió al evento
     */
    @Transactional
    @CacheEvict(value = {"asistencias", "estadisticasEvento"}, allEntries = true)
    public Asistencia marcarNoAsistio(Long asistenciaId) {
        log.info("Marcando asistencia como NO asistió: {}", asistenciaId);

        Asistencia asistencia = obtenerAsistenciaPorId(asistenciaId);
        asistencia.marcarNoAsistio();

        return asistenciaRepository.save(asistencia);
    }

    public Asistencia obtenerAsistenciaPorId(Long id) {
        return asistenciaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asistencia", "id", id));
    }

    @Cacheable(value = "asistencias", key = "#eventoId")
    public List<Asistencia> obtenerAsistenciasPorEvento(Long eventoId) {
        log.debug("Obteniendo asistencias del evento: {}", eventoId);
        return asistenciaRepository.findByEventoId(eventoId);
    }

    public List<Asistencia> obtenerAsistenciasPorParticipante(Long participanteId) {
        log.debug("Obteniendo asistencias del participante: {}", participanteId);
        return asistenciaRepository.findByParticipanteId(participanteId);
    }

    /**
     * Obtiene estadísticas de un evento (con caché)
     */
    @Cacheable(value = "estadisticasEvento", key = "#eventoId")
    public EstadisticasEvento obtenerEstadisticas(Long eventoId) {
        log.debug("Obteniendo estadísticas del evento: {}", eventoId);

        Evento evento = eventoService.obtenerEventoPorId(eventoId);

        long totalRegistrados = asistenciaRepository.countByEventoId(eventoId);
        long confirmados = asistenciaRepository.countByEventoIdAndEstado(eventoId, EstadoAsistencia.CONFIRMADO);
        long cancelados = asistenciaRepository.countByEventoIdAndEstado(eventoId, EstadoAsistencia.CANCELADO);
        long asistieron = asistenciaRepository.countByEventoIdAndEstado(eventoId, EstadoAsistencia.ASISTIO);
        long noAsistieron = asistenciaRepository.countByEventoIdAndEstado(eventoId, EstadoAsistencia.NO_ASISTIO);

        return EstadisticasEvento.builder()
                .eventoId(eventoId)
                .nombreEvento(evento.getNombre())
                .capacidadMaxima(evento.getCapacidadMaxima())
                .totalRegistrados((int) totalRegistrados)
                .confirmados((int) confirmados)
                .cancelados((int) cancelados)
                .asistieron((int) asistieron)
                .noAsistieron((int) noAsistieron)
                .cuposDisponibles(evento.getCuposDisponibles())
                .porcentajeOcupacion(evento.getPorcentajeOcupacion())
                .build();
    }

    @Transactional
    @CacheEvict(value = {"asistencias", "estadisticasEvento"}, allEntries = true)
    public void eliminarAsistencia(Long id) {
        log.info("Eliminando asistencia con ID: {}", id);

        Asistencia asistencia = obtenerAsistenciaPorId(id);

        // Si estaba confirmada, decrementar el contador
        if (asistencia.getEstado() == EstadoAsistencia.CONFIRMADO) {
            eventoService.decrementarParticipantes(asistencia.getEventoId());
        }

        asistenciaRepository.deleteById(id);
    }
}

