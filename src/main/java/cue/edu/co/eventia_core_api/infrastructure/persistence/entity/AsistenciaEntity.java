package cue.edu.co.eventia_core_api.infrastructure.persistence.entity;

import cue.edu.co.eventia_core_api.domain.model.EstadoAsistencia;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entidad JPA para Asistencia
 * Representa la relación entre Participante y Evento
 */
@Entity
@Table(name = "asistencias",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_asistencia_evento_participante",
                         columnNames = {"evento_id", "participante_id"})
    },
    indexes = {
        @Index(name = "idx_asistencia_evento", columnList = "evento_id"),
        @Index(name = "idx_asistencia_participante", columnList = "participante_id"),
        @Index(name = "idx_asistencia_estado", columnList = "estado")
    }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AsistenciaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evento_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_asistencia_evento"))
    private EventoEntity evento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participante_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_asistencia_participante"))
    private ParticipanteEntity participante;

    @CreationTimestamp
    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EstadoAsistencia estado = EstadoAsistencia.CONFIRMADO;

    @Column(columnDefinition = "TEXT")
    private String notas;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
}

