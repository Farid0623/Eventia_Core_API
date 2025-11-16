package cue.edu.co.eventia_core_api.domain.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para estadísticas de un evento
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstadisticasEvento {
    private Long eventoId;
    private String nombreEvento;
    private Integer capacidadMaxima;
    private Integer totalRegistrados;
    private Integer confirmados;
    private Integer cancelados;
    private Integer asistieron;
    private Integer noAsistieron;
    private Integer cuposDisponibles;
    private Double porcentajeOcupacion;
}

