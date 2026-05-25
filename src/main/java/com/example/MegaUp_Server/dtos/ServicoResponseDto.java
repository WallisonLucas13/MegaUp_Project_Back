package com.example.MegaUp_Server.dtos;

import com.example.MegaUp_Server.models.Etapa;
import com.example.MegaUp_Server.models.Material;
import com.example.MegaUp_Server.models.Servico;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO de resposta para Servico. Substitui a exposição direta da entidade JPA,
 * evitando acoplamento de schema e problemas de lazy-loading fora de transação.
 * A conversão ocorre dentro da transação para garantir que coleções lazy sejam
 * inicializadas corretamente.
 */
public record ServicoResponseDto(
        Long id,
        String nome,
        String desc,
        BigDecimal valorFinal,
        BigDecimal valorTotalMateriais,
        BigDecimal maoDeObra,
        Integer desconto,
        Integer porcentagemEntrada,
        BigDecimal valorEntrada,
        String formaPagamentoEntrada,
        BigDecimal valorPagamentoFinal,
        String formaPagamentoFinal,
        List<Material> materiais,
        List<Etapa> etapas) {

    public static ServicoResponseDto from(Servico s) {
        return new ServicoResponseDto(
                s.getId(),
                s.getNome(),
                s.getDesc(),
                s.getValorFinal(),
                s.getValorTotalMateriais(),
                s.getMaoDeObra(),
                s.getDesconto(),
                s.getPorcentagemEntrada(),
                s.getValorEntrada(),
                s.getFormaPagamentoEntrada() != null ? s.getFormaPagamentoEntrada().name() : null,
                s.getValorPagamentoFinal(),
                s.getFormaPagamentoFinal() != null ? s.getFormaPagamentoFinal().name() : null,
                s.getMateriais() != null ? new ArrayList<>(s.getMateriais()) : List.of(),
                s.getEtapas() != null ? new ArrayList<>(s.getEtapas()) : List.of()
        );
    }
}
