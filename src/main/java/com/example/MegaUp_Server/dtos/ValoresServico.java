package com.example.MegaUp_Server.dtos;

import com.example.MegaUp_Server.models.Etapa;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class ValoresServico {

    private int valor;

    private int valorTotalMateriais;

    private int valorFinal;

    private int desconto;

    private com.example.MegaUp_Server.dtos.Entrada entrada;

    private com.example.MegaUp_Server.dtos.PagamentoFinal pagamentoFinal;

    private List<Etapa> etapas;
}
