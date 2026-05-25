package com.example.MegaUp_Server.controlers;

import com.example.MegaUp_Server.security.service.JwtService;
import com.example.MegaUp_Server.services.ClienteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testa validacoes Bean Validation no ClienteController.
 * Filtros JWT desabilitados — foco e apenas na camada de validacao.
 */
@WebMvcTest(controllers = ClienteController.class)
@AutoConfigureMockMvc(addFilters = false)
class ClienteControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClienteService service;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void criarClienteSemNomeRetornaBadRequest() throws Exception {
        String body = "{\"tel\": \"11999999999\", \"bairro\": \"Centro\", \"endereco\": \"Rua A\"}";

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void criarClienteComNomeVazioRetornaBadRequest() throws Exception {
        String body = "{\"nome\": \"\", \"tel\": \"11999999999\", \"bairro\": \"Centro\", \"endereco\": \"Rua A\"}";

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void criarClienteSemTelefoneRetornaBadRequest() throws Exception {
        String body = "{\"nome\": \"Joao\", \"bairro\": \"Centro\", \"endereco\": \"Rua A\"}";

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void criarClienteSemBairroRetornaBadRequest() throws Exception {
        String body = "{\"nome\": \"Joao\", \"tel\": \"11999999999\", \"endereco\": \"Rua A\"}";

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void editarClienteSemNomeRetornaBadRequest() throws Exception {
        String body = "{\"tel\": \"11999999999\", \"bairro\": \"Centro\", \"endereco\": \"Rua B\"}";

        mockMvc.perform(put("/clientes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }
}
