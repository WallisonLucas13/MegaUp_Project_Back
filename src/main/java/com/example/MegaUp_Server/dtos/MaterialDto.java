package com.example.MegaUp_Server.dtos;

import com.example.MegaUp_Server.models.Material;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record MaterialDto(
        @NotBlank String nome,
        @Min(1) int quant,
        @DecimalMin(value = "0.01", inclusive = true) @Digits(integer = 17, fraction = 2) BigDecimal valor) {

    public Material transform() {
        return new Material(nome, quant, valor);
    }
}
