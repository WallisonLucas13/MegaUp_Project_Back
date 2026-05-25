package com.example.MegaUp_Server.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record Entrada(
        @NotNull @Min(0) @Max(100) Integer porcentagem,
        BigDecimal valor,
        @NotBlank String formaPagamento) {}
