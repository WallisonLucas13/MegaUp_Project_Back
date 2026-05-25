package com.example.MegaUp_Server.services;

import com.example.MegaUp_Server.exceptions.EntidadeJaExisteException;
import com.example.MegaUp_Server.exceptions.ObjetoInexistenteException;
import com.example.MegaUp_Server.models.Cliente;
import com.example.MegaUp_Server.models.Servico;
import com.example.MegaUp_Server.repositories.ClienteRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class ClienteService {

    private final ClienteRepository repository;

    @Transactional(readOnly = true)
    public List<Cliente> listarTodos() throws RuntimeException{
        return this.repository.findAll();
    }

    @Transactional(readOnly = true)
    public Cliente getById(Long id) throws ObjetoInexistenteException {
        return repository.findById(id).orElseThrow(() -> new ObjetoInexistenteException("Inexistente"));
    }

    @Transactional
    public void salvarCliente(Cliente cliente) throws RuntimeException{
        log.info("Salvando cliente [nome={}]", cliente.getNome());
        boolean exist = repository.existsByNome(cliente.getNome());
        if(exist){throw new EntidadeJaExisteException("Cliente já cadastrado!");}
        this.repository.save(cliente);
        log.info("Cliente salvo com sucesso [nome={}]", cliente.getNome());
    }

    @Transactional
    public void atualizarCliente(Cliente cliente, Long id) throws ObjetoInexistenteException{
        log.info("Atualizando cliente [id={}]", id);
        Cliente existente = repository.findById(id)
                .orElseThrow(() -> new ObjetoInexistenteException("Cliente Inexistente!"));

        boolean nomeExistente = repository.existsByNomeAndIdNot(cliente.getNome(), id);
        if(nomeExistente){throw new EntidadeJaExisteException("Já existe um cliente com esse nome!");}

        existente.setNome(cliente.getNome());
        existente.setTel(cliente.getTel());
        existente.setBairro(cliente.getBairro());
        existente.setEndereco(cliente.getEndereco());
        repository.save(existente);
        log.info("Cliente atualizado com sucesso [id={}, nome={}]", id, existente.getNome());
    }
    @Transactional
    public void apagarCliente(Long id) throws ObjetoInexistenteException{
        log.info("Removendo cliente [id={}]", id);
        if(!repository.existsById(id)){
            throw new ObjetoInexistenteException("Cliente Inexistente!");
        }
        repository.deleteById(id);
        log.info("Cliente removido com sucesso [id={}]", id);
    }

    @Transactional
    public void addServicoInClient(Servico servico, Long idCliente){

        Cliente cliente = repository.findById(idCliente)
                .orElseThrow(() -> new ObjetoInexistenteException("Inexistente"));

        servico.setCliente(cliente);

        List<Servico> novaLista = cliente.getServicos() == null
                ? new ArrayList<>()
                : new ArrayList<>(cliente.getServicos());
        novaLista.add(servico);
        cliente.setServicos(novaLista);

        repository.save(cliente);
    }

    @Transactional(readOnly = true)
    public List<Servico> listarTodosServicos(Long id){
        Cliente cliente = repository.findById(id)
                .orElseThrow(() -> new ObjetoInexistenteException("Inexistente"));

        return cliente.getServicos();
    }
}
