package com.example.MegaUp_Server.services;

import com.example.MegaUp_Server.exceptions.ObjetoInexistenteException;
import com.example.MegaUp_Server.models.Cliente;
import com.example.MegaUp_Server.models.Servico;
import com.example.MegaUp_Server.repositories.ClienteRepository;
import jakarta.transaction.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {
    @Autowired
    private ClienteRepository repository;

    @Transactional
    public List<Cliente> listarTodos() throws RuntimeException{
        return this.repository.findAll();
    }

    @Transactional
    public Cliente getById(Long id) throws ObjetoInexistenteException {
        return repository.findById(id).orElseThrow(() -> new ObjetoInexistenteException("Inexistente"));
    }

    @Transactional
    public void salvarCliente(Cliente cliente) throws RuntimeException{

        boolean exist = repository.existsByNome(cliente.getNome());
        if(exist){throw new RuntimeException();}

        this.repository.save(cliente);
    }

    @Transactional
    public void atualizarCliente(Cliente cliente, Long id) throws ObjetoInexistenteException{

        boolean exist = repository.existsById(id);

        if(!exist){throw new ObjetoInexistenteException("Cliente Inexistente!");}

        cliente.setId(id);
        repository.save(cliente);
    }
    @Transactional
    public void apagarCliente(Long id) throws ObjetoInexistenteException{

        if(!repository.existsById(id)){
            throw new ObjetoInexistenteException("Cliente Inexistente!");
        }

        repository.deleteById(id);
    }

    @Transactional
    public void addServicoInClient(Servico servico, Long idCliente){

        Cliente cliente = repository.findById(idCliente)
                .orElseThrow(() -> new ObjetoInexistenteException("Inexistente"));

        List<Servico> novaLista = cliente.getServicos();
        novaLista.add(servico);
        cliente.setServicos(novaLista);

        repository.save(cliente);
    }

    @Transactional
    public List<Servico> listarTodosServicos(Long id){
        Cliente cliente = repository.findById(id)
                .orElseThrow(() -> new ObjetoInexistenteException("Inexistente"));

        return cliente.getServicos();
    }
}
