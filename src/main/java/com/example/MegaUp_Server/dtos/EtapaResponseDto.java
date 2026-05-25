package com.example.MegaUp_Server.dtos;

import com.example.MegaUp_Server.models.Etapa;

import java.math.BigDecimal;

public record EtapaResponseDto(Long id, Long iden, BigDecimal valor) {

    public static EtapaResponseDto from(Etapa etapa) {
        return new EtapaResponseDto(etapa.getId(), etapa.getIden(), etapa.getValor());
    }
}
