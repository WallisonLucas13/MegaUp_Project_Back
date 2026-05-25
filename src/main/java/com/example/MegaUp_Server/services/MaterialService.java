package com.example.MegaUp_Server.services;

import com.example.MegaUp_Server.exceptions.ObjetoInexistenteException;
import com.example.MegaUp_Server.models.Material;
import com.example.MegaUp_Server.repositories.MaterialRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class MaterialService {

    private static final int MONEY_SCALE = 2;
    private static final RoundingMode MONEY_ROUNDING = RoundingMode.HALF_UP;

    private final MaterialRepository repository;
    private final ServicoService servicoService;

    @Transactional(readOnly = true)
    public List<Material> listarTodos(Long idServico) throws RuntimeException{
        return servicoService.listarTodosMateriais(idServico);
    }

    @Transactional
    public void salvarMaterial(Material material, Long idServico) throws RuntimeException{
        log.info("Salvando material [nome={}, servicoId={}]", material.getNome(), idServico);
        this.servicoService.addMaterialInServico(material, idServico);
        log.info("Material salvo com sucesso [nome={}, servicoId={}]", material.getNome(), idServico);
    }

    @Transactional
    public void atualizarMaterial(Material material, Long id) throws ObjetoInexistenteException {
        log.info("Atualizando material [id={}]", id);
        Material atual = repository.findById(id)
                .orElseThrow(() -> new ObjetoInexistenteException("Material Inexistente!"));

        atual.setNome(material.getNome());
        atual.setQuant(material.getQuant());
        atual.setValor(normalize(material.getValor()));
        Material salvo = repository.save(atual);
        log.info("Material atualizado com sucesso [id={}, nome={}]", id, atual.getNome());

        if(salvo.getServico() != null && salvo.getServico().getId() != null){
            servicoService.recalcularValoresServico(salvo.getServico().getId());
        }
    }

    @Transactional
    public void apagarMaterial(Long id) throws ObjetoInexistenteException{
        log.info("Removendo material [id={}]", id);
        Material material = repository.findById(id)
                .orElseThrow(() -> new ObjetoInexistenteException("Material Inexistente!"));

        servicoService.apagarMaterialInServico(material);

        // orphanRemoval no Servico.materiais apaga o material ao remover da coleção.
        // Se o material estiver órfão (sem serviço), deleta explicitamente.
        if (material.getServico() == null) {
            repository.delete(material);
        }
        log.info("Material removido com sucesso [id={}]", id);
    }

    private BigDecimal normalize(BigDecimal value){
        if(value == null){
            return BigDecimal.ZERO.setScale(MONEY_SCALE, MONEY_ROUNDING);
        }
        return value.setScale(MONEY_SCALE, MONEY_ROUNDING);
    }
}
