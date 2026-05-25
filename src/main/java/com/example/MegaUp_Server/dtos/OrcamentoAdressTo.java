package com.example.MegaUp_Server.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OrcamentoAdressTo(
        @NotBlank @Email String adress,
        @NotNull Long idCliente,
        boolean ocultarMateriais,
        boolean ocultarMaoDeObra,
        boolean ocultarDesconto) {}
