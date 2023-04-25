package com.example.MegaUp_Server.services;

import com.example.MegaUp_Server.models.Etapa;
import com.example.MegaUp_Server.repositories.EtapaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EtapaService {

    @Autowired
    private EtapaRepository repository;

    @Transactional
    public void deleteEtapa(Long id){
        Etapa etapa = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(""));

        repository.delete(etapa);
    }
}
