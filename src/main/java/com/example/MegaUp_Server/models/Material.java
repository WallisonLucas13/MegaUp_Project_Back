package com.example.MegaUp_Server.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Setter
@Getter
public class Material {

    public Material(String nome, int quant, int valor){
        this.nome = nome;
        this.quant = quant;
        this.valor = valor;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String nome;

    @Column
    private int quant;

    @Column
    private int valor;

    @ManyToOne
    private com.example.MegaUp_Server.models.Servico servico;
}
