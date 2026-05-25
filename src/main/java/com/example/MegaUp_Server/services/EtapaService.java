package com.example.MegaUp_Server.services;

import com.example.MegaUp_Server.models.Etapa;
import com.example.MegaUp_Server.models.Servico;
import com.example.MegaUp_Server.repositories.ServicoRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class EtapaService {

    private final ServicoRepository servicoRepository;

    @Transactional
    public void deleteEtapa(Long id) {
        log.info("Removendo etapa [id={}]", id);
        // Busca o Servico pai pela etapa para navegar pelo agregado correto
        Servico servico = servicoRepository.findByEtapaId(id)
                .orElseThrow(() -> new IllegalArgumentException("Etapa Inexistente!"));

        List<Etapa> etapas = new ArrayList<>(servico.getEtapas());
        etapas.removeIf(e -> e.getId().equals(id));

        // Resequencia o iden para evitar gaps e duplicatas ao adicionar novas etapas
        for (int i = 0; i < etapas.size(); i++) {
            etapas.get(i).setIden((long) (i + 1));
        }
        etapas.sort(Comparator.comparingLong(Etapa::getIden));

        servico.setEtapas(etapas);
        servicoRepository.save(servico);
        log.info("Etapa removida com sucesso [id={}] do serviço [id={}]", id, servico.getId());
    }
}
