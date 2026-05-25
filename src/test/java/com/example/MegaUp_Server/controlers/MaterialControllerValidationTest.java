package com.example.MegaUp_Server.controlers;

import com.example.MegaUp_Server.security.service.JwtService;
import com.example.MegaUp_Server.services.MaterialService;
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
 * Testa validacoes Bean Validation no MaterialController.
 * Filtros JWT desabilitados — foco e apenas na camada de validacao.
 */
@WebMvcTest(controllers = MaterialController.class)
@AutoConfigureMockMvc(addFilters = false)
class MaterialControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MaterialService service;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void criarMaterialSemNomeRetornaBadRequest() throws Exception {
        String body = "{\"quant\": 2, \"valor\": 10.50}";

        mockMvc.perform(post("/materiais/servico/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void criarMaterialComNomeVazioRetornaBadRequest() throws Exception {
        String body = "{\"nome\": \"\", \"quant\": 2, \"valor\": 10.50}";

        mockMvc.perform(post("/materiais/servico/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void criarMaterialComQuantidadeZeroRetornaBadRequest() throws Exception {
        String body = "{\"nome\": \"Tinta\", \"quant\": 0, \"valor\": 10.50}";

        mockMvc.perform(post("/materiais/servico/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void criarMaterialComQuantidadeNegativaRetornaBadRequest() throws Exception {
        String body = "{\"nome\": \"Tinta\", \"quant\": -3, \"valor\": 10.50}";

        mockMvc.perform(post("/materiais/servico/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void criarMaterialComValorNegativoRetornaBadRequest() throws Exception {
        String body = "{\"nome\": \"Tinta\", \"quant\": 2, \"valor\": -5.00}";

        mockMvc.perform(post("/materiais/servico/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void editarMaterialComQuantidadeZeroRetornaBadRequest() throws Exception {
        String body = "{\"nome\": \"Parafuso\", \"quant\": 0, \"valor\": 1.00}";

        mockMvc.perform(put("/materiais/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }
}
