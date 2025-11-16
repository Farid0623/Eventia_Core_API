package cue.edu.co.eventia_core_api.infrastructure.persistence.entity;

import cue.edu.co.eventia_core_api.domain.model.TipoDocumento;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidad JPA para Participante
 */
@Entity
@Table(name = "participantes",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_participante_email", columnNames = "email"),
        @UniqueConstraint(name = "uk_participante_documento", columnNames = "documento")
    },
    indexes = {
        @Index(name = "idx_participante_email", columnList = "email"),
        @Index(name = "idx_participante_documento", columnList = "documento")
    }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipanteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String apellido;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(length = 20)
    private String telefono;

    @Column(nullable = false, unique = true, length = 50)
    private String documento;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento", nullable = false, length = 30)
    private TipoDocumento tipoDocumento;

    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @OneToMany(mappedBy = "participante", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<AsistenciaEntity> asistencias = new HashSet<>();
}

