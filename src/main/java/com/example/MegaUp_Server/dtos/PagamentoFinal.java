package com.example.MegaUp_Server.dtos;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record PagamentoFinal(BigDecimal valor, @NotBlank String formaPagamento) {}
