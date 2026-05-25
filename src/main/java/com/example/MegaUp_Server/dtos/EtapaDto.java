package com.example.MegaUp_Server.dtos;

import com.example.MegaUp_Server.models.Etapa;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * DTO para criação de Etapa. Recebe apenas o valor — id e iden são
 * gerados pelo servidor, impedindo manipulação de IDs pelo cliente (BE-10).
 */
public record EtapaDto(
        @NotNull @DecimalMin(value = "0.01", inclusive = true) BigDecimal valor) {

    public Etapa toEtapa() {
        Etapa etapa = new Etapa();
        etapa.setValor(valor);
        return etapa;
    }
}
