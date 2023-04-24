package com.example.MegaUp_Server.services;

import com.example.MegaUp_Server.exceptions.ObjetoInexistenteException;
import com.example.MegaUp_Server.models.Material;
import com.example.MegaUp_Server.repositories.MaterialRepository;
import jakarta.transaction.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MaterialService {

    @Autowired
    private MaterialRepository repository;

    @Autowired
    private com.example.MegaUp_Server.services.ServicoService servicoService;

    @Transactional
    public List<Material> listarTodos(Long idServico) throws RuntimeException{
        return servicoService.listarTodosMateriais(idServico);
    }

    @Transactional
    public void salvarMaterial(Material material, Long idServico) throws RuntimeException{

        this.servicoService.addMaterialInServico(material, idServico);
    }

    @Transactional
    public void atualizarMaterial(Material material, Long id) throws ObjetoInexistenteException {

        boolean exist = repository.existsById(id);

        if(!exist){throw new ObjetoInexistenteException("Material Inexistente!");}

        material.setId(id);
        repository.save(material);
    }

    @Transactional
    public void apagarMaterial(Long id) throws ObjetoInexistenteException{

        if(!repository.existsById(id)){
            throw new ObjetoInexistenteException("Cliente Inexistente!");
        }

        servicoService.apagarMaterialInServico(repository.findById(id)
                .orElseThrow(() -> new ObjetoInexistenteException("Inexistente")));

        repository.deleteById(id);
    }
}
