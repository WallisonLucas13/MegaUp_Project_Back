package com.example.MegaUp_Server.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record MaoDeObraDto(
        @NotNull @DecimalMin(value = "0.00", inclusive = true) BigDecimal valor) {
}
