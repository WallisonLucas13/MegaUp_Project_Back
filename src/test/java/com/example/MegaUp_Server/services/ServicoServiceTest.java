package com.example.MegaUp_Server.services;

import com.example.MegaUp_Server.dtos.EntradaRequestDto;
import com.example.MegaUp_Server.dtos.PagamentoFinal;
import com.example.MegaUp_Server.enums.FormaPagamento;
import com.example.MegaUp_Server.models.Etapa;
import com.example.MegaUp_Server.models.Material;
import com.example.MegaUp_Server.models.Servico;
import com.example.MegaUp_Server.repositories.ServicoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServicoServiceTest {

    @Mock
    private ServicoRepository repository;

    @Mock
    private ClienteService clienteService;

    @Mock
    private SendMailService sendMailService;

    @InjectMocks
    private ServicoService service;

    // -------------------------------------------------------
    // recalcValores: calculo basico com desconto e entrada
    // -------------------------------------------------------

    @Test
    void setMaoDeObraRecalculaValoresComDescontoEEntrada() {
        Servico servico = new Servico();
        servico.setId(1L);
        servico.setValorTotalMateriais(new BigDecimal("50.00"));
        servico.setDesconto(10);
        servico.setPorcentagemEntrada(20);

        when(repository.findById(1L)).thenReturn(Optional.of(servico));

        service.setMaoDeObra(new BigDecimal("100.00"), 1L);

        // subtotal = 150; desconto 10% = 135; entrada 20% de 135 = 27; pagFinal = 108
        assertThat(servico.getValorFinal()).isEqualByComparingTo("135.00");
        assertThat(servico.getValorEntrada()).isEqualByComparingTo("27.00");
        assertThat(servico.getValorPagamentoFinal()).isEqualByComparingTo("108.00");
    }

    @Test
    void setMaoDeObraSemDescontoSemEntradaIgualaSubtotal() {
        Servico servico = new Servico();
        servico.setId(4L);
        servico.setValorTotalMateriais(new BigDecimal("30.00"));
        // desconto e entrada ficam com defaults = 0

        when(repository.findById(4L)).thenReturn(Optional.of(servico));

        service.setMaoDeObra(new BigDecimal("70.00"), 4L);

        assertThat(servico.getValorFinal()).isEqualByComparingTo("100.00");
        assertThat(servico.getValorEntrada()).isEqualByComparingTo("0.00");
        assertThat(servico.getValorPagamentoFinal()).isEqualByComparingTo("100.00");
    }

    @Test
    void setMaoDeObraComDesconto100RetornaZero() {
        Servico servico = new Servico();
        servico.setId(5L);
        servico.setValorTotalMateriais(new BigDecimal("0.00"));
        servico.setDesconto(100);

        when(repository.findById(5L)).thenReturn(Optional.of(servico));

        service.setMaoDeObra(new BigDecimal("200.00"), 5L);

        assertThat(servico.getValorFinal()).isEqualByComparingTo("0.00");
        assertThat(servico.getValorPagamentoFinal()).isEqualByComparingTo("0.00");
    }

    @Test
    void setMaoDeObraComEntrada100TodaValorEEntrada() {
        Servico servico = new Servico();
        servico.setId(6L);
        servico.setValorTotalMateriais(new BigDecimal("0.00"));
        servico.setDesconto(0);
        servico.setPorcentagemEntrada(100);

        when(repository.findById(6L)).thenReturn(Optional.of(servico));

        service.setMaoDeObra(new BigDecimal("150.00"), 6L);

        assertThat(servico.getValorFinal()).isEqualByComparingTo("150.00");
        assertThat(servico.getValorEntrada()).isEqualByComparingTo("150.00");
        assertThat(servico.getValorPagamentoFinal()).isEqualByComparingTo("0.00");
    }

    // -------------------------------------------------------
    // addEtapa: teto e sequencia de IDs
    // -------------------------------------------------------

    @Test
    void addEtapaRespeitaTetoDisponivel() {
        Servico servico = new Servico();
        servico.setId(2L);
        servico.setValorPagamentoFinal(new BigDecimal("200.00"));

        Etapa etapaExistente = new Etapa();
        etapaExistente.setIden(1L);
        etapaExistente.setValor(new BigDecimal("150.00"));

        List<Etapa> etapas = new ArrayList<>();
        etapas.add(etapaExistente);
        servico.setEtapas(etapas);

        when(repository.findByIdForUpdate(2L)).thenReturn(Optional.of(servico));

        Etapa etapaOk = new Etapa();
        etapaOk.setValor(new BigDecimal("40.00"));

        service.addEtapa(2L, etapaOk);

        assertThat(servico.getEtapas()).hasSize(2);
        assertThat(servico.getEtapas().get(1).getIden()).isEqualTo(2L);

        Etapa etapaExcesso = new Etapa();
        etapaExcesso.setValor(new BigDecimal("60.00"));

        assertThatThrownBy(() -> service.addEtapa(2L, etapaExcesso))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void addEtapaExatamenteNoTetoDevePersistir() {
        Servico servico = new Servico();
        servico.setId(7L);
        servico.setValorPagamentoFinal(new BigDecimal("100.00"));
        servico.setEtapas(new ArrayList<>());

        when(repository.findByIdForUpdate(7L)).thenReturn(Optional.of(servico));

        Etapa etapa = new Etapa();
        etapa.setValor(new BigDecimal("100.00"));

        service.addEtapa(7L, etapa);

        assertThat(servico.getEtapas()).hasSize(1);
        assertThat(servico.getEtapas().get(0).getValor()).isEqualByComparingTo("100.00");
    }

    // -------------------------------------------------------
    // recalcularValoresServico: recalcula total de materiais
    // -------------------------------------------------------

    @Test
    void recalcularValoresServicoAtualizaTotais() {
        Servico servico = new Servico();
        servico.setId(3L);
        servico.setMaoDeObra(new BigDecimal("80.00"));
        servico.setDesconto(0);
        servico.setPorcentagemEntrada(0);

        Material material = new Material();
        material.setValor(new BigDecimal("10.00"));
        material.setQuant(3);

        List<Material> materiais = new ArrayList<>();
        materiais.add(material);
        servico.setMateriais(materiais);

        when(repository.findById(3L)).thenReturn(Optional.of(servico));

        service.recalcularValoresServico(3L);

        assertThat(servico.getValorTotalMateriais()).isEqualByComparingTo("30.00");
        assertThat(servico.getValorFinal()).isEqualByComparingTo("110.00");
    }

    @Test
    void recalcularComListaMaterialNulaMantemValores() {
        Servico servico = new Servico();
        servico.setId(8L);
        servico.setMaoDeObra(new BigDecimal("50.00"));
        servico.setDesconto(0);
        servico.setPorcentagemEntrada(0);
        servico.setMateriais(null);

        when(repository.findById(8L)).thenReturn(Optional.of(servico));

        service.recalcularValoresServico(8L);

        assertThat(servico.getValorTotalMateriais()).isEqualByComparingTo("0.00");
        assertThat(servico.getValorFinal()).isEqualByComparingTo("50.00");
    }

    @Test
    void recalcularComMultiplosMateriais() {
        Servico servico = new Servico();
        servico.setId(9L);
        servico.setMaoDeObra(new BigDecimal("100.00"));
        servico.setDesconto(0);
        servico.setPorcentagemEntrada(0);

        Material m1 = new Material();
        m1.setValor(new BigDecimal("5.50"));
        m1.setQuant(4);  // 22.00

        Material m2 = new Material();
        m2.setValor(new BigDecimal("10.00"));
        m2.setQuant(3);  // 30.00

        servico.setMateriais(List.of(m1, m2));

        when(repository.findById(9L)).thenReturn(Optional.of(servico));

        service.recalcularValoresServico(9L);

        assertThat(servico.getValorTotalMateriais()).isEqualByComparingTo("52.00");
        assertThat(servico.getValorFinal()).isEqualByComparingTo("152.00");
    }

    // -------------------------------------------------------
    // T2: sendEntrada e sendFormaPagamentoFinal
    // -------------------------------------------------------

    @Test
    void sendEntradaDefinePercentualERecalculaValores() {
        Servico servico = new Servico();
        servico.setId(10L);
        servico.setMaoDeObra(new BigDecimal("100.00"));
        servico.setValorTotalMateriais(new BigDecimal("0.00"));
        servico.setDesconto(0);
        servico.setPorcentagemEntrada(0);

        when(repository.findById(10L)).thenReturn(Optional.of(servico));

        EntradaRequestDto dto = new EntradaRequestDto(50, "PIX");
        service.sendEntrada(dto, 10L);

        // valorFinal = 100; entrada 50% = 50; pagFinal = 50
        assertThat(servico.getPorcentagemEntrada()).isEqualTo(50);
        assertThat(servico.getFormaPagamentoEntrada()).isEqualTo(FormaPagamento.PIX);
        assertThat(servico.getValorEntrada()).isEqualByComparingTo("50.00");
        assertThat(servico.getValorPagamentoFinal()).isEqualByComparingTo("50.00");
    }

    @Test
    void sendEntradaComPercentualZeroNaoAlteraValorPagamentoFinal() {
        Servico servico = new Servico();
        servico.setId(11L);
        servico.setMaoDeObra(new BigDecimal("200.00"));
        servico.setValorTotalMateriais(new BigDecimal("0.00"));
        servico.setDesconto(0);
        servico.setPorcentagemEntrada(0);

        when(repository.findById(11L)).thenReturn(Optional.of(servico));

        EntradaRequestDto dto = new EntradaRequestDto(0, "DINHEIRO");
        service.sendEntrada(dto, 11L);

        assertThat(servico.getValorEntrada()).isEqualByComparingTo("0.00");
        assertThat(servico.getValorPagamentoFinal()).isEqualByComparingTo("200.00");
    }

    @Test
    void sendFormaPagamentoFinalAtualizaFormaPagamento() {
        Servico servico = new Servico();
        servico.setId(12L);
        servico.setMaoDeObra(new BigDecimal("50.00"));
        servico.setValorTotalMateriais(new BigDecimal("0.00"));
        servico.setDesconto(0);
        servico.setPorcentagemEntrada(0);

        when(repository.findById(12L)).thenReturn(Optional.of(servico));

        PagamentoFinal pagamento = new PagamentoFinal(null, "CREDITO");
        service.sendFormaPagamentoFinal(pagamento, 12L);

        assertThat(servico.getFormaPagamentoFinal()).isEqualTo(FormaPagamento.CREDITO);
    }
}
