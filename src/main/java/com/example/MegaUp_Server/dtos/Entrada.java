package com.example.MegaUp_Server.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Entrada {

    public Entrada(String porcentagem, String valor, String formaPagamento) {
        this.porcentagem = porcentagem;
        this.valor = valor;
        this.formaPagamento = formaPagamento;
    }

    private String porcentagem;

    private String valor;

    private String formaPagamento;
}
