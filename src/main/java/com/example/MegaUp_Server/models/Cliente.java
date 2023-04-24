package com.example.MegaUp_Server.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Cliente {

    public Cliente(String nome, String tel, String bairro, String endereco){
        this.tel = tel;
        this.nome = nome;
        this.bairro = bairro;
        this.endereco = endereco;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String nome;

    @Column
    private String bairro;

    @Column
    private String endereco;

    @Column
    private String tel;

    @OneToMany(cascade = CascadeType.ALL)
    private List<com.example.MegaUp_Server.models.Servico> servicos;
}
