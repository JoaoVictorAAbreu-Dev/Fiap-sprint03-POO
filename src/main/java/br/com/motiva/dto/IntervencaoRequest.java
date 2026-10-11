package br.com.motiva.dto;
import br.com.motiva.model.NivelPrioridade;
import br.com.motiva.model.TipoIntervencao;
import jakarta.validation.constraints.*;
public record IntervencaoRequest(@NotNull @Positive Long idTrecho, @NotNull @Positive Long idEquipe, @NotNull TipoIntervencao tipoIntervencao, @NotNull NivelPrioridade nivelPrioridade, @NotNull @DecimalMin("0.0") Double alturaProjetadaCm, @NotBlank @Size(max=1000) String justificativa, @NotBlank String status) {}
