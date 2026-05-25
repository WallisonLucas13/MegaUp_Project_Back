package com.example.MegaUp_Server.services;

import com.example.MegaUp_Server.dtos.Entrada;
import com.example.MegaUp_Server.dtos.EntradaRequestDto;
import com.example.MegaUp_Server.dtos.EtapaResponseDto;
import com.example.MegaUp_Server.dtos.OrcamentoAdressTo;
import com.example.MegaUp_Server.dtos.PagamentoFinal;
import com.example.MegaUp_Server.dtos.ServicoResponseDto;
import com.example.MegaUp_Server.dtos.ValoresServico;
import com.example.MegaUp_Server.enums.FormaPagamento;
import com.example.MegaUp_Server.exceptions.ObjetoInexistenteException;
import com.example.MegaUp_Server.models.Etapa;
import com.example.MegaUp_Server.models.Material;
import com.example.MegaUp_Server.models.Servico;
import com.example.MegaUp_Server.repositories.ServicoRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class ServicoService {

    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");
    private static final int MONEY_SCALE = 2;
    private static final RoundingMode MONEY_ROUNDING = RoundingMode.HALF_UP;

    private final ServicoRepository repository;
    private final ClienteService clienteService;
    private final SendMailService sendMailService;

    @Transactional(readOnly = true)
    public List<ServicoResponseDto> listarTodos(Long idCliente) throws RuntimeException{
        // S3: mapeamento para DTO ocorre dentro da transação para garantir inicialização das coleções lazy
        return clienteService.listarTodosServicos(idCliente)
                .stream()
                .map(ServicoResponseDto::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void salvarServico(Servico servico, Long idCliente) throws RuntimeException{
        log.info("Salvando serviço [nome={}] para cliente [id={}]", servico.getNome(), idCliente);
        this.clienteService.addServicoInClient(servico, idCliente);
        log.info("Serviço salvo com sucesso [nome={}, clienteId={}]", servico.getNome(), idCliente);
    }

    @Transactional
    public void atualizarServico(Servico servico, Long id) throws ObjetoInexistenteException{
        log.info("Atualizando serviço [id={}]", id);
        Servico existente = repository.findById(id)
                .orElseThrow(() -> new ObjetoInexistenteException("Serviço Inexistente!"));

        // Atualiza APENAS os campos editáveis pelo usuário.
        // Nunca substituir a entidade inteira: zeraria maoDeObra, materiais, valorFinal, etc.
        existente.setNome(servico.getNome());
        existente.setDesc(servico.getDesc());
        repository.save(existente);
        log.info("Serviço atualizado com sucesso [id={}, nome={}]", id, existente.getNome());
    }
    @Transactional
    public void apagarServico(Long id) throws ObjetoInexistenteException{
        log.info("Removendo serviço [id={}]", id);
        if(!repository.existsById(id)){
            throw new ObjetoInexistenteException("Serviço Inexistente!");
        }
        repository.deleteById(id);
        log.info("Serviço removido com sucesso [id={}]", id);
    }

    @Transactional
    public void addMaterialInServico(Material material, Long idServico){

        Servico servico = repository.findById(idServico)
                .orElseThrow(() -> new ObjetoInexistenteException("Inexistente"));

        material.setValor(normalize(material.getValor()));
        material.setServico(servico);

        List<Material> novaLista = servico.getMateriais() == null
            ? new ArrayList<>()
            : new ArrayList<>(servico.getMateriais());
        novaLista.add(material);
        servico.setMateriais(novaLista);
        servico.setValorTotalMateriais(calcularValorTotalMateriais(servico.getMateriais()));
        recalcValores(servico);
        repository.save(servico);

    }

    @Transactional
    public void apagarMaterialInServico(Material material){

        Servico servico = material.getServico();
        if (servico == null) return;

        List<Material> materiais = new ArrayList<>(servico.getMateriais());
        materiais.remove(material);
        servico.setMateriais(materiais);
        servico.setValorTotalMateriais(calcularValorTotalMateriais(materiais));
        recalcValores(servico);
        repository.save(servico);
    }

    @Transactional(readOnly = true)
    public List<Material> listarTodosMateriais(Long id){
        Servico servico = repository.findById(id)
                .orElseThrow(() -> new ObjetoInexistenteException("Inexistente"));

        return servico.getMateriais();
    }

    private BigDecimal calcularValorTotalMateriais(List<Material> materiais){

        BigDecimal valor = BigDecimal.ZERO;

        if(materiais == null || materiais.isEmpty()){
            return valor;
        }

        for (int i = 0; i < materiais.size(); i++) {
            BigDecimal valorMaterial = normalize(materiais.get(i).getValor());
            BigDecimal quant = BigDecimal.valueOf(materiais.get(i).getQuant());
            valor = valor.add(valorMaterial.multiply(quant));
        }

        return normalize(valor);
    }

    @Transactional
    public void setMaoDeObra(BigDecimal maoDeObra, Long id){
        log.info("Definindo mão de obra do serviço [id={}, valor={}]", id, maoDeObra);
        Servico servico = repository.findById(id)
                .orElseThrow(() -> new ObjetoInexistenteException("Inexistente"));

        servico.setMaoDeObra(normalize(maoDeObra));
        recalcValores(servico);
        repository.save(servico);
        log.info("Mão de obra definida com sucesso [id={}, valor={}]", id, servico.getMaoDeObra());
    }

    @Transactional(readOnly = true)
    public ValoresServico getValores(Long id) {

        Servico servico = repository.findById(id)
                .orElseThrow(() -> new ObjetoInexistenteException("Inexistente"));

        // Calcula os valores sem mutar a entidade gerenciada (evita UPDATE silencioso via dirty-check)
        BigDecimal maoDeObra     = normalize(servico.getMaoDeObra());
        BigDecimal materiais     = normalize(servico.getValorTotalMateriais());
        BigDecimal subtotal      = maoDeObra.add(materiais);

        int descontoPercent      = normalizePercent(servico.getDesconto());
        BigDecimal descontoRate  = BigDecimal.valueOf(descontoPercent).divide(ONE_HUNDRED, 4, MONEY_ROUNDING);
        BigDecimal valorFinal    = subtotal.subtract(subtotal.multiply(descontoRate)).setScale(MONEY_SCALE, MONEY_ROUNDING);

        int entradaPercent       = normalizePercent(servico.getPorcentagemEntrada());
        BigDecimal entradaRate   = BigDecimal.valueOf(entradaPercent).divide(ONE_HUNDRED, 4, MONEY_ROUNDING);
        BigDecimal valorEntrada  = valorFinal.multiply(entradaRate).setScale(MONEY_SCALE, MONEY_ROUNDING);
        BigDecimal valorPgtoFinal = valorFinal.subtract(valorEntrada).setScale(MONEY_SCALE, MONEY_ROUNDING);

        ValoresServico valoresServico = new ValoresServico();
        valoresServico.setValor(maoDeObra);
        valoresServico.setValorTotalMateriais(materiais);
        valoresServico.setValorFinal(valorFinal);
        valoresServico.setDesconto(servico.getDesconto());
        valoresServico.setEntrada(new Entrada(
                servico.getPorcentagemEntrada(),
                valorEntrada,
                servico.getFormaPagamentoEntrada().name()));
        valoresServico.setPagamentoFinal(new PagamentoFinal(
                valorPgtoFinal,
                servico.getFormaPagamentoFinal().name()));

        List<Etapa> etapas = servico.getEtapas() == null
                ? new ArrayList<>()
                : new ArrayList<>(servico.getEtapas());
        etapas.sort(Comparator.comparingLong(Etapa::getIden));
        valoresServico.setEtapas(etapas.stream().map(EtapaResponseDto::from).toList());

        return valoresServico;
    }

    @Transactional
    public void aplicarDesconto(Long id, int desconto){
        log.info("Aplicando desconto de {}% ao serviço [id={}]", desconto, id);
        Servico servico = repository.findById(id)
                .orElseThrow(() -> new ObjetoInexistenteException("Inexistente"));

        servico.setDesconto(desconto);
        recalcValores(servico);
        repository.save(servico);
        log.info("Desconto de {}% aplicado com sucesso ao serviço [id={}]", desconto, id);
    }

    @Transactional
    public void sendOrcamento(Long id, OrcamentoAdressTo adress){
        log.info("Enviando orçamento do serviço [id={}]", id);
        Servico servico = repository.findById(id)
                .orElseThrow(() -> new ObjetoInexistenteException("Inexistente"));

        sendMailService.createMailAndSendWithAttachments(adress, servico);
        log.info("Orçamento do serviço [id={}] enviado com sucesso", id);
    }

    @Transactional
    public void sendEntrada(EntradaRequestDto entrada, Long idServico){
        log.info("Definindo entrada do serviço [id={}, porcentagem={}%]", idServico, entrada.porcentagem());
        Servico servico = repository.findById(idServico)
                .orElseThrow(() -> new ObjetoInexistenteException("Inexistente"));

        servico.setPorcentagemEntrada(entrada.porcentagem());
        servico.setFormaPagamentoEntrada(formatarFormaPagamento(entrada.formaPagamento()));
        recalcValores(servico);
        repository.save(servico);
        log.info("Entrada definida com sucesso [id={}, porcentagem={}%]", idServico, entrada.porcentagem());
    }

    @Transactional
    public void sendFormaPagamentoFinal(PagamentoFinal pagamentoFinal, Long idServico){
        log.info("Definindo pagamento final do serviço [id={}, forma={}]", idServico, pagamentoFinal.formaPagamento());
        Servico servico = repository.findById(idServico)
                .orElseThrow(() -> new ObjetoInexistenteException("Inexistente"));

        servico.setFormaPagamentoFinal(formatarFormaPagamento(pagamentoFinal.formaPagamento()));
        repository.save(servico);
        log.info("Pagamento final definido com sucesso [id={}]", idServico);
    }

    @Transactional
    public void recalcularValoresServico(Long idServico){
        Servico servico = repository.findById(idServico)
                .orElseThrow(() -> new ObjetoInexistenteException("Inexistente"));

        servico.setValorTotalMateriais(calcularValorTotalMateriais(servico.getMateriais()));
        recalcValores(servico);
        repository.save(servico);
    }

    @Transactional
    public void addEtapa(Long idServico, Etapa etapa) throws IllegalArgumentException{
        log.info("Adicionando etapa ao serviço [id={}, valor={}]", idServico, etapa.getValor());
        Servico servico = repository.findByIdForUpdate(idServico)
                .orElseThrow(() -> new ObjetoInexistenteException("Inexistente"));

        if(servico.getEtapas() == null){
            servico.setEtapas(new ArrayList<>());
        }

        BigDecimal tetoGastos = normalize(servico.getValorPagamentoFinal()).subtract(calcEtapas(servico.getEtapas()));
        BigDecimal valorEtapa = normalize(etapa.getValor());
        etapa.setValor(valorEtapa);

        if(valorEtapa.compareTo(tetoGastos) <= 0){
            List<Etapa> update = new ArrayList<>(servico.getEtapas());
            etapa.setIden(update.size() + 1L);
            update.add(etapa);
            update.sort(Comparator.comparingLong(Etapa::getIden));

            servico.setEtapas(update);
            repository.save(servico);
            log.info("Etapa adicionada com sucesso ao serviço [id={}]", idServico);
            return;
        }

        log.warn("Valor da etapa [{}] ultrapassou o teto disponível [{}] no serviço [id={}]",
                valorEtapa, tetoGastos, idServico);
        throw new IllegalArgumentException("Valor máximo ultrapassado!");

    }

    private BigDecimal calcEtapas(List<Etapa> etapas){
        if(etapas == null || etapas.isEmpty()){
            return BigDecimal.ZERO;
        }
        return etapas.stream()
                .map(etapa -> normalize(etapa.getValor()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private FormaPagamento formatarFormaPagamento(String forma) {
        if (forma == null) return FormaPagamento.NENHUMA;
        try {
            return FormaPagamento.valueOf(forma.toUpperCase());
        } catch (IllegalArgumentException e) {
            return FormaPagamento.NENHUMA;
        }
    }

    private BigDecimal normalize(BigDecimal value){
        if(value == null){
            return BigDecimal.ZERO.setScale(MONEY_SCALE, MONEY_ROUNDING);
        }
        return value.setScale(MONEY_SCALE, MONEY_ROUNDING);
    }

    private int normalizePercent(Integer percent){
        return percent == null ? 0 : percent;
    }

    private void recalcValores(Servico servico){
        BigDecimal maoDeObra = normalize(servico.getMaoDeObra());
        BigDecimal materiais = normalize(servico.getValorTotalMateriais());
        BigDecimal subtotal = maoDeObra.add(materiais);

        int descontoPercent = normalizePercent(servico.getDesconto());
        BigDecimal descontoRate = BigDecimal.valueOf(descontoPercent).divide(ONE_HUNDRED, 4, MONEY_ROUNDING);
        BigDecimal valorFinal = subtotal.subtract(subtotal.multiply(descontoRate)).setScale(MONEY_SCALE, MONEY_ROUNDING);

        int entradaPercent = normalizePercent(servico.getPorcentagemEntrada());
        BigDecimal entradaRate = BigDecimal.valueOf(entradaPercent).divide(ONE_HUNDRED, 4, MONEY_ROUNDING);
        BigDecimal valorEntrada = valorFinal.multiply(entradaRate).setScale(MONEY_SCALE, MONEY_ROUNDING);
        BigDecimal valorPagamentoFinal = valorFinal.subtract(valorEntrada).setScale(MONEY_SCALE, MONEY_ROUNDING);

        servico.setValorFinal(valorFinal);
        servico.setValorEntrada(valorEntrada);
        servico.setValorPagamentoFinal(valorPagamentoFinal);
    }
}
