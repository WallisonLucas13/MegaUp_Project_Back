package com.example.MegaUp_Server.dtos;

import com.example.MegaUp_Server.models.Cliente;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClienteDto {
    @NotBlank
    private String nome;
    @NotBlank
    private String tel;

    @NotBlank
    private String bairro;

    @NotBlank
    private String endereco;

    public Cliente tranform(){
        return new Cliente(this.nome, this.tel, this.bairro, this.endereco);
    }
}
