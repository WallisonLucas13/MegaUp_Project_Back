package com.example.MegaUp_Server.dtos;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
public class ValoresServico {

    private BigDecimal valor;

    private BigDecimal valorTotalMateriais;

    private BigDecimal valorFinal;

    private Integer desconto;

    private com.example.MegaUp_Server.dtos.Entrada entrada;

    private com.example.MegaUp_Server.dtos.PagamentoFinal pagamentoFinal;

    private List<EtapaResponseDto> etapas;
}
