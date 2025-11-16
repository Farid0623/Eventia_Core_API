package cue.edu.co.eventia_core_api.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrarAsistenciaRequest {

    @NotNull(message = "El ID del evento es obligatorio")
    private Long eventoId;

    @NotNull(message = "El ID del participante es obligatorio")
    private Long participanteId;

    @Size(max = 500, message = "Las notas no pueden exceder 500 caracteres")
    private String notas;
}

