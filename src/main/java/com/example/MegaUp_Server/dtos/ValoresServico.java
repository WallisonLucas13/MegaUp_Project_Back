package com.example.MegaUp_Server.dtos;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ValoresServico {

    private int valor;

    private int valorTotalMateriais;

    private int valorFinal;

    private int desconto;

    private com.example.MegaUp_Server.dtos.Entrada entrada;

    private com.example.MegaUp_Server.dtos.PagamentoFinal pagamentoFinal;
}
