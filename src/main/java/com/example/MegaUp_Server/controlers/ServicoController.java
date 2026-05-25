package com.example.MegaUp_Server.controlers;

import com.example.MegaUp_Server.dtos.DescontoDto;
import com.example.MegaUp_Server.dtos.EntradaRequestDto;
import com.example.MegaUp_Server.dtos.EtapaDto;
import com.example.MegaUp_Server.dtos.MaoDeObraDto;
import com.example.MegaUp_Server.dtos.OrcamentoAdressTo;
import com.example.MegaUp_Server.dtos.PagamentoFinal;
import com.example.MegaUp_Server.dtos.ServicoDto;
import com.example.MegaUp_Server.dtos.ServicoResponseDto;
import com.example.MegaUp_Server.dtos.ValoresServico;
import com.example.MegaUp_Server.services.EtapaService;
import com.example.MegaUp_Server.services.ServicoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/servicos")
@RequiredArgsConstructor
@Log4j2
public class ServicoController {

    private final ServicoService service;
    private final EtapaService etapaService;

    @PostMapping("/cliente/{clienteId}")
    public ResponseEntity<String> save(@RequestBody @Valid ServicoDto dto, @PathVariable(name = "clienteId") Long clienteId) {
        log.info("POST /servicos/cliente/{} - criando serviço [nome={}]", clienteId, dto.getNome());
        this.service.salvarServico(dto.transform(), clienteId);
        log.info("POST /servicos/cliente/{} - serviço criado com sucesso [nome={}]", clienteId, dto.getNome());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<ServicoResponseDto>> listar(@PathVariable(name = "clienteId") Long clienteId) {
        log.info("GET /servicos/cliente/{} - listando serviços", clienteId);
        List<ServicoResponseDto> result = service.listarTodos(clienteId);
        log.info("GET /servicos/cliente/{} - {} serviço(s) retornado(s)", clienteId, result.size());
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> atualizar(@RequestBody @Valid ServicoDto dto,
            @PathVariable(name = "id") @NotNull Long id) {
        log.info("PUT /servicos/{} - atualizando serviço [nome={}]", id, dto.getNome());
        service.atualizarServico(dto.transform(), id);
        log.info("PUT /servicos/{} - serviço atualizado com sucesso", id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> remover(@PathVariable(name = "id") @NotNull Long id) {
        log.info("DELETE /servicos/{} - removendo serviço", id);
        service.apagarServico(id);
        log.info("DELETE /servicos/{} - serviço removido com sucesso", id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // I2: usa MaoDeObraDto dedicado em vez de ValoresServico completo
    @PutMapping("/{id}/mao-de-obra")
    public ResponseEntity<String> maoDeObra(@RequestBody @Valid MaoDeObraDto maoDeObra, @PathVariable(name = "id") Long id) {
        log.info("PUT /servicos/{}/mao-de-obra - valor={}", id, maoDeObra.valor());
        service.setMaoDeObra(maoDeObra.valor(), id);
        log.info("PUT /servicos/{}/mao-de-obra - mão de obra atualizada com sucesso", id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/{id}/valores")
    public ResponseEntity<ValoresServico> getvalores(@PathVariable(name = "id") Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(service.getValores(id));
    }

    @PutMapping("/{id}/desconto")
    public ResponseEntity<String> desconto(@PathVariable(name = "id") Long id, @RequestBody @Valid DescontoDto desconto) {
        log.info("PUT /servicos/{}/desconto - porcentagem={}%", id, desconto.porcentagem());
        service.aplicarDesconto(id, desconto.porcentagem());
        log.info("PUT /servicos/{}/desconto - desconto aplicado com sucesso", id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // I1: usa EntradaRequestDto que não expõe o campo 'valor' calculado pelo servidor
    @PutMapping("/{id}/entrada")
    public ResponseEntity<String> entrada(@PathVariable("id") Long id, @RequestBody @Valid EntradaRequestDto entrada) {
        log.info("PUT /servicos/{}/entrada - porcentagem={}%", id, entrada.porcentagem());
        service.sendEntrada(entrada, id);
        log.info("PUT /servicos/{}/entrada - entrada definida com sucesso", id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/{id}/pagamento-final")
    public ResponseEntity<String> pagamentoFinal(@PathVariable("id") Long id, @RequestBody @Valid PagamentoFinal pagamentoFinal) {
        log.info("PUT /servicos/{}/pagamento-final - forma={}", id, pagamentoFinal.formaPagamento());
        service.sendFormaPagamentoFinal(pagamentoFinal, id);
        log.info("PUT /servicos/{}/pagamento-final - pagamento final definido com sucesso", id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/{id}/orcamento")
    public ResponseEntity<String> sendOrcamento(@PathVariable("id") Long id, @RequestBody @Valid OrcamentoAdressTo adressTo) {
        log.info("POST /servicos/{}/orcamento - enviando orçamento", id);
        service.sendOrcamento(id, adressTo);
        log.info("POST /servicos/{}/orcamento - orçamento enviado com sucesso", id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/{id}/etapas")
    public ResponseEntity<String> addEtapa(@PathVariable("id") Long id, @RequestBody @Valid EtapaDto etapaDto) {
        log.info("POST /servicos/{}/etapas - adicionando etapa", id);
        service.addEtapa(id, etapaDto.toEtapa());
        log.info("POST /servicos/{}/etapas - etapa adicionada com sucesso", id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/etapas/{id}")
    public ResponseEntity<String> removeEtapa(@PathVariable("id") Long id) {
        log.info("DELETE /servicos/etapas/{} - removendo etapa", id);
        etapaService.deleteEtapa(id);
        log.info("DELETE /servicos/etapas/{} - etapa removida com sucesso", id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
