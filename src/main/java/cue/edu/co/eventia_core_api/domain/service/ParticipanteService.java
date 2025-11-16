package cue.edu.co.eventia_core_api.domain.service;

import cue.edu.co.eventia_core_api.domain.model.Participante;
import cue.edu.co.eventia_core_api.domain.repository.ParticipanteRepository;
import cue.edu.co.eventia_core_api.exception.DuplicateResourceException;
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
 * Servicio de dominio para la gestión de Participantes
 * Contiene la lógica de negocio relacionada con participantes
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ParticipanteService {

    private final ParticipanteRepository participanteRepository;

    @Transactional
    @CacheEvict(value = "participantes", allEntries = true)
    public Participante crearParticipante(Participante participante) {
        log.info("Creando nuevo participante: {}", participante.getEmail());

        // Validar que no exista otro participante con el mismo email
        if (participanteRepository.existsByEmail(participante.getEmail())) {
            throw new DuplicateResourceException("Participante", "email", participante.getEmail());
        }

        // Validar que no exista otro participante con el mismo documento
        if (participanteRepository.existsByDocumento(participante.getDocumento())) {
            throw new DuplicateResourceException("Participante", "documento", participante.getDocumento());
        }

        participante.setFechaCreacion(LocalDateTime.now());

        return participanteRepository.save(participante);
    }

    @Transactional
    @CacheEvict(value = "participantes", allEntries = true)
    public Participante actualizarParticipante(Long id, Participante participanteActualizado) {
        log.info("Actualizando participante con ID: {}", id);

        Participante participanteExistente = obtenerParticipantePorId(id);

        // Validar email único (si cambió)
        if (!participanteExistente.getEmail().equals(participanteActualizado.getEmail()) &&
            participanteRepository.existsByEmail(participanteActualizado.getEmail())) {
            throw new DuplicateResourceException("Participante", "email", participanteActualizado.getEmail());
        }

        // Validar documento único (si cambió)
        if (!participanteExistente.getDocumento().equals(participanteActualizado.getDocumento()) &&
            participanteRepository.existsByDocumento(participanteActualizado.getDocumento())) {
            throw new DuplicateResourceException("Participante", "documento", participanteActualizado.getDocumento());
        }

        participanteActualizado.setId(id);
        participanteActualizado.setFechaCreacion(participanteExistente.getFechaCreacion());
        participanteActualizado.setFechaActualizacion(LocalDateTime.now());

        return participanteRepository.save(participanteActualizado);
    }

    @Cacheable(value = "participantes", key = "#id")
    public Participante obtenerParticipantePorId(Long id) {
        log.debug("Obteniendo participante por ID: {}", id);
        return participanteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Participante", "id", id));
    }

    public Participante obtenerParticipantePorEmail(String email) {
        log.debug("Obteniendo participante por email: {}", email);
        return participanteRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Participante", "email", email));
    }

    public Participante obtenerParticipantePorDocumento(String documento) {
        log.debug("Obteniendo participante por documento: {}", documento);
        return participanteRepository.findByDocumento(documento)
                .orElseThrow(() -> new ResourceNotFoundException("Participante", "documento", documento));
    }

    @Cacheable(value = "participantes")
    public List<Participante> obtenerTodosLosParticipantes() {
        log.debug("Obteniendo todos los participantes");
        return participanteRepository.findAll();
    }

    @Transactional
    @CacheEvict(value = "participantes", allEntries = true)
    public void eliminarParticipante(Long id) {
        log.info("Eliminando participante con ID: {}", id);

        if (!participanteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Participante", "id", id);
        }

        participanteRepository.deleteById(id);
    }
}

