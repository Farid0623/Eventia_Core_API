package cue.edu.co.eventia_core_api.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidad de dominio para Evento
 * Representa un evento en el sistema
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Evento {

    private Long id;
    private String nombre;
    private String descripcion;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private String ubicacion;
    private Integer capacidadMaxima;
    private Integer participantesRegistrados;
    private EstadoEvento estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    @Builder.Default
    private Set<Asistencia> asistencias = new HashSet<>();

    /**
     * Verifica si el evento tiene cupos disponibles
     */
    public boolean tieneCapacidadDisponible() {
        return participantesRegistrados < capacidadMaxima;
    }

    /**
     * Verifica si el evento está activo
     */
    public boolean estaActivo() {
        return estado == EstadoEvento.ACTIVO;
    }

    /**
     * Calcula el número de cupos disponibles
     */
    public int getCuposDisponibles() {
        return capacidadMaxima - participantesRegistrados;
    }

    /**
     * Calcula el porcentaje de ocupación
     */
    public double getPorcentajeOcupacion() {
        if (capacidadMaxima == 0) return 0;
        return (double) participantesRegistrados / capacidadMaxima * 100;
    }

    /**
     * Verifica si el evento ya finalizó
     */
    public boolean haFinalizado() {
        return LocalDateTime.now().isAfter(fechaFin);
    }
}

