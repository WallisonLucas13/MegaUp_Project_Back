package com.example.MegaUp_Server.dtos;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OrcamentoAdressTo{

    private String adress;

    private Long idCliente;

    private boolean ocultarMateriais;

    private boolean ocultarMaoDeObra;

    private boolean ocultarDesconto;
}
