package com.example.MegaUp_Server.controlers;

import com.example.MegaUp_Server.security.service.JwtService;
import com.example.MegaUp_Server.services.EtapaService;
import com.example.MegaUp_Server.services.ServicoService;
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
 * Testa validacoes dos endpoints de Servico sem subir o contexto completo.
 * addFilters=false desabilita o filtro JWT para focar apenas na validacao de beans.
 */
@WebMvcTest(controllers = ServicoController.class)
@AutoConfigureMockMvc(addFilters = false)
class ServicoControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ServicoService service;

    @MockBean
    private EtapaService etapaService;

    // Beans de seguranca necessarios para o contexto do WebMvcTest
    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void descontoInvalidoRetornaBadRequest() throws Exception {
        String body = "{\"porcentagem\": -1}";

        mockMvc.perform(put("/servicos/1/desconto")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void descontoMaiorQue100RetornaBadRequest() throws Exception {
        String body = "{\"porcentagem\": 150}";

        mockMvc.perform(put("/servicos/1/desconto")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void entradaComPercentualMaiorQue100RetornaBadRequest() throws Exception {
        String body = "{\"porcentagem\": 120, \"formaPagamento\": \"PIX\"}";

        mockMvc.perform(put("/servicos/1/entrada")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void entradaComPercentualNegativoRetornaBadRequest() throws Exception {
        String body = "{\"porcentagem\": -5, \"formaPagamento\": \"PIX\"}";

        mockMvc.perform(put("/servicos/1/entrada")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void orcamentoComEmailInvalidoRetornaBadRequest() throws Exception {
        String body = "{\"adress\": \"invalido\", \"idCliente\": 1, " +
                "\"ocultarMateriais\": false, \"ocultarMaoDeObra\": false, \"ocultarDesconto\": false}";

        mockMvc.perform(post("/servicos/1/orcamento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void etapaComValorZeroRetornaBadRequest() throws Exception {
        String body = "{\"valor\": 0}";

        mockMvc.perform(post("/servicos/1/etapas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }
}
