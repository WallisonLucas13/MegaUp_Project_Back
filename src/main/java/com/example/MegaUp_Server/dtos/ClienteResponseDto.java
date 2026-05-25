package com.example.MegaUp_Server.dtos;

import com.example.MegaUp_Server.models.Cliente;

public record ClienteResponseDto(Long id, String nome, String tel, String bairro, String endereco) {

    public static ClienteResponseDto from(Cliente c) {
        return new ClienteResponseDto(c.getId(), c.getNome(), c.getTel(), c.getBairro(), c.getEndereco());
    }
}
