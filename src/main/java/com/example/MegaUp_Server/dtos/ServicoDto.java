package com.example.MegaUp_Server.dtos;

import com.example.MegaUp_Server.models.Servico;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServicoDto {

    @NotBlank
    private String nome;
    private String desc;

    public Servico transform(){
        return new Servico(nome, desc);
    }
}
