package com.example.MegaUp_Server.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO de entrada (request) para o endpoint PUT /Servicos/Entrada/{id}.
 * Não expõe o campo 'valor' que é calculado internamente pelo servidor.
 */
public record EntradaRequestDto(
        @NotNull @Min(0) @Max(100) Integer porcentagem,
        @NotBlank String formaPagamento) {
}
