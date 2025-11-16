package cue.edu.co.eventia_core_api.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidad de dominio para Participante
 * Representa un participante que puede registrarse en eventos
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Participante {

    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private String documento;
    private TipoDocumento tipoDocumento;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    @Builder.Default
    private Set<Asistencia> asistencias = new HashSet<>();

    /**
     * Obtiene el nombre completo del participante
     */
    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    /**
     * Verifica si el participante está registrado en un evento específico
     */
    public boolean estaRegistradoEn(Long eventoId) {
        return asistencias.stream()
                .anyMatch(a -> a.getEventoId().equals(eventoId) &&
                              a.getEstado() == EstadoAsistencia.CONFIRMADO);
    }
}

