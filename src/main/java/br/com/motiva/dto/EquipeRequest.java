package br.com.motiva.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
public record EquipeRequest(@NotBlank @Size(max=100) String nome, @NotBlank @Size(max=100) String especialidade, @NotNull Boolean ativa) {}
