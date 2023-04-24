package com.example.MegaUp_Server.dtos;

import com.example.MegaUp_Server.models.Material;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MaterialDto {

    private String nome;

    private int quant;

    private int valor;

    public Material transform(){
        return new Material(nome, quant, valor);
    }

}
