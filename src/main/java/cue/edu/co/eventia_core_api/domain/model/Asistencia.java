package cue.edu.co.eventia_core_api.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad de dominio para Asistencia
 * Representa la relación entre un Participante y un Evento
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Asistencia {

    private Long id;
    private Long eventoId;
    private Long participanteId;
    private LocalDateTime fechaRegistro;
    private EstadoAsistencia estado;
    private String notas;
    private LocalDateTime fechaActualizacion;

    /**
     * Confirma la asistencia
     */
    public void confirmar() {
        this.estado = EstadoAsistencia.CONFIRMADO;
        this.fechaActualizacion = LocalDateTime.now();
    }

    /**
     * Cancela la asistencia
     */
    public void cancelar() {
        this.estado = EstadoAsistencia.CANCELADO;
        this.fechaActualizacion = LocalDateTime.now();
    }

    /**
     * Marca como asistió
     */
    public void marcarAsistio() {
        this.estado = EstadoAsistencia.ASISTIO;
        this.fechaActualizacion = LocalDateTime.now();
    }

    /**
     * Marca como no asistió
     */
    public void marcarNoAsistio() {
        this.estado = EstadoAsistencia.NO_ASISTIO;
        this.fechaActualizacion = LocalDateTime.now();
    }

    /**
     * Verifica si la asistencia está confirmada
     */
    public boolean estaConfirmada() {
        return estado == EstadoAsistencia.CONFIRMADO;
    }
}

