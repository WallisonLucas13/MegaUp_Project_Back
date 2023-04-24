package com.example.MegaUp_Server.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PagamentoFinal {

    public PagamentoFinal(String valor,String formaPagamento) {
        this.valor = valor;
        this.formaPagamento = formaPagamento;
    }

    private String valor;

    private String formaPagamento;
}
