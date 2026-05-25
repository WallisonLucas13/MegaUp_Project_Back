package com.example.MegaUp_Server.dtos;

import com.example.MegaUp_Server.models.Cliente;
import jakarta.validation.constraints.NotBlank;

public record ClienteDto(
        @NotBlank String nome,
        @NotBlank String tel,
        @NotBlank String bairro,
        @NotBlank String endereco) {

    public Cliente transform() {
        return new Cliente(nome, tel, bairro, endereco);
    }
}
