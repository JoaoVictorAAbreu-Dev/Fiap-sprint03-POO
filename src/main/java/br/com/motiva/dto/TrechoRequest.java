package br.com.motiva.dto;
import br.com.motiva.model.TipoCrescimento;
import jakarta.validation.constraints.*;
public record TrechoRequest(@NotNull @PositiveOrZero Integer quilometro, @NotBlank @Size(max=20) String sentido, @NotNull @DecimalMin("0.0") Double alturaVegetacaoCm, @NotNull TipoCrescimento tipoCrescimento, @NotNull Boolean areaSensivel, @NotNull Boolean monitoradoIot, @Size(max=50) String codigoSensor) {}
