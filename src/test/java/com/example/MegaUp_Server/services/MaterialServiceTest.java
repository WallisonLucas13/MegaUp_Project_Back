package com.example.MegaUp_Server.services;

import com.example.MegaUp_Server.exceptions.ObjetoInexistenteException;
import com.example.MegaUp_Server.models.Material;
import com.example.MegaUp_Server.models.Servico;
import com.example.MegaUp_Server.repositories.MaterialRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MaterialServiceTest {

    @Mock
    private MaterialRepository repository;

    @Mock
    private ServicoService servicoService;

    @InjectMocks
    private MaterialService service;

    @Test
    void atualizarMaterialRecalculaServicoRelacionado() throws Exception {
        Servico servico = new Servico();
        servico.setId(10L);

        Material existente = new Material();
        existente.setId(5L);
        existente.setServico(servico);

        when(repository.findById(5L)).thenReturn(Optional.of(existente));
        when(repository.save(any(Material.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Material atualizado = new Material();
        atualizado.setNome("Tinta");
        atualizado.setQuant(2);
        atualizado.setValor(new BigDecimal("12.50"));

        service.atualizarMaterial(atualizado, 5L);

        verify(servicoService).recalcularValoresServico(10L);
    }

    @Test
    void atualizarMaterialSemServicaNaoRecalcula() throws Exception {
        // Material sem servico associado: nao deve chamar recalculo
        Material existente = new Material();
        existente.setId(6L);
        existente.setServico(null);

        when(repository.findById(6L)).thenReturn(Optional.of(existente));
        when(repository.save(any(Material.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Material atualizado = new Material();
        atualizado.setNome("Parafuso");
        atualizado.setQuant(10);
        atualizado.setValor(new BigDecimal("0.50"));

        service.atualizarMaterial(atualizado, 6L);

        verify(servicoService, never()).recalcularValoresServico(any());
    }

    @Test
    void atualizarMaterialInexistenteLancaErro() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        Material m = new Material();
        m.setNome("X");
        m.setQuant(1);
        m.setValor(BigDecimal.ONE);

        assertThatThrownBy(() -> service.atualizarMaterial(m, 99L))
                .isInstanceOf(ObjetoInexistenteException.class);
    }

    @Test
    void apagarMaterialInexistenteLancaErro() {
        when(repository.findById(77L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.apagarMaterial(77L))
                .isInstanceOf(ObjetoInexistenteException.class);
    }

    @Test
    void atualizarMaterialNormalizaValorParaDuasCasas() throws Exception {
        Servico servico = new Servico();
        servico.setId(20L);

        Material existente = new Material();
        existente.setId(8L);
        existente.setServico(servico);

        when(repository.findById(8L)).thenReturn(Optional.of(existente));
        when(repository.save(any(Material.class))).thenAnswer(invocation -> {
            Material saved = invocation.getArgument(0);
            // valor deve estar com scale 2
            assert saved.getValor().scale() == 2 : "Escala deve ser 2";
            return saved;
        });

        Material atualizado = new Material();
        atualizado.setNome("Cabo");
        atualizado.setQuant(3);
        atualizado.setValor(new BigDecimal("7.5")); // sem trailing zero

        service.atualizarMaterial(atualizado, 8L);

        verify(repository).save(argThat(m -> m.getValor().scale() == 2));
    }

    // -------------------------------------------------------
    // T3: apagarMaterial com servico associado
    // -------------------------------------------------------

    @Test
    void apagarMaterialComServicoDelega() {
        Servico servico = new Servico();
        servico.setId(30L);

        Material material = new Material();
        material.setId(15L);
        material.setServico(servico);

        when(repository.findById(15L)).thenReturn(Optional.of(material));

        service.apagarMaterial(15L);

        // deve delegar a remoção ao ServicoService (que usa orphanRemoval)
        verify(servicoService).apagarMaterialInServico(material);
        // Não deve chamar repository.delete() — o orphanRemoval cuida disso
        verify(repository, never()).delete(any(Material.class));
    }

    @Test
    void apagarMaterialSemServicoDeleteDireto() {
        Material material = new Material();
        material.setId(16L);
        material.setServico(null);

        when(repository.findById(16L)).thenReturn(Optional.of(material));

        service.apagarMaterial(16L);

        verify(servicoService).apagarMaterialInServico(material);
        verify(repository).delete(material);
    }
}
