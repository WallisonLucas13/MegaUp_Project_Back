package com.example.MegaUp_Server.services;

import com.example.MegaUp_Server.exceptions.ObjetoInexistenteException;
import com.example.MegaUp_Server.models.Cliente;
import com.example.MegaUp_Server.repositories.ClienteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository repository;

    @InjectMocks
    private ClienteService service;

    @Test
    void atualizarClienteComNomeDuplicadoLancaErro() {
        Cliente cliente = new Cliente("Joao", "123", "Centro", "Rua A");

        when(repository.findById(1L)).thenReturn(Optional.of(cliente));
        when(repository.existsByNomeAndIdNot("Joao", 1L)).thenReturn(true);

        assertThatThrownBy(() -> service.atualizarCliente(cliente, 1L))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void atualizarClienteComNomeUnicoNaoLancaErro() {
        Cliente cliente = new Cliente("Joao", "123", "Centro", "Rua A");

        when(repository.findById(1L)).thenReturn(Optional.of(cliente));
        when(repository.existsByNomeAndIdNot("Joao", 1L)).thenReturn(false);
        when(repository.save(any(Cliente.class))).thenReturn(cliente);

        assertThatNoException().isThrownBy(() -> service.atualizarCliente(cliente, 1L));
        verify(repository).save(any(Cliente.class));
    }

    @Test
    void atualizarClienteInexistenteLancaObjetoInexistente() {
        Cliente cliente = new Cliente("Maria", "456", "Bela Vista", "Av B");

        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.atualizarCliente(cliente, 99L))
                .isInstanceOf(ObjetoInexistenteException.class);
    }

    @Test
    void apagarClienteInexistenteLancaObjetoInexistente() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.apagarCliente(99L))
                .isInstanceOf(ObjetoInexistenteException.class);
    }
}
