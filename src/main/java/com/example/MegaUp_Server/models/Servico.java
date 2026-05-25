package com.example.MegaUp_Server.models;

import com.example.MegaUp_Server.enums.FormaPagamento;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Entity
@NoArgsConstructor
@Setter
@Getter
public class Servico {

    public Servico(String nome, String desc){
        this.desc = desc;
        this.nome = nome;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String nome;
    @Column(name = "sobre")
    private String desc;
    @Column(precision = 19, scale = 2)
    private BigDecimal valorFinal = BigDecimal.ZERO;

    @Column(precision = 19, scale = 2)
    private BigDecimal valorTotalMateriais = BigDecimal.ZERO;

    @Column(precision = 19, scale = 2)
    private BigDecimal maoDeObra = BigDecimal.ZERO;

    @Column
    private Integer desconto = 0;

    @Column
    private Integer porcentagemEntrada = 0;

    @Column(precision = 19, scale = 2)
    private BigDecimal valorEntrada = BigDecimal.ZERO;

    @Column
    @Enumerated(EnumType.STRING)
    private FormaPagamento formaPagamentoEntrada = FormaPagamento.NENHUMA;

    @Column(precision = 19, scale = 2)
    private BigDecimal valorPagamentoFinal = BigDecimal.ZERO;

    @Column
    @Enumerated(EnumType.STRING)
    private FormaPagamento formaPagamentoFinal = FormaPagamento.NENHUMA;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private com.example.MegaUp_Server.models.Cliente cliente;

    @OneToMany(mappedBy = "servico", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<com.example.MegaUp_Server.models.Material> materiais;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "servico_id")
    private List<Etapa> etapas;

}
