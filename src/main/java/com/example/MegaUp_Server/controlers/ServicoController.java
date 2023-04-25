package com.example.MegaUp_Server.controlers;

import com.example.MegaUp_Server.dtos.DescontoDto;
import com.example.MegaUp_Server.dtos.Entrada;
import com.example.MegaUp_Server.dtos.OrcamentoAdressTo;
import com.example.MegaUp_Server.dtos.PagamentoFinal;
import com.example.MegaUp_Server.dtos.ServicoDto;
import com.example.MegaUp_Server.dtos.ValoresServico;
import com.example.MegaUp_Server.exceptions.ObjetoInexistenteException;
import com.example.MegaUp_Server.models.Etapa;
import com.example.MegaUp_Server.models.Servico;
import com.example.MegaUp_Server.services.EtapaService;
import com.example.MegaUp_Server.services.ServicoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Servicos")
@CrossOrigin("*")
public class ServicoController {

    @Autowired
    private ServicoService service;

    @Autowired
    private EtapaService etapaService;

    @PostMapping("/New")
    public ResponseEntity<String> save(@RequestBody @Valid ServicoDto dto, @RequestParam(name = "id") Long idCliente){
        try {
            this.service.salvarServico(dto.transform(), idCliente);
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        catch(RuntimeException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/Todos")
    public ResponseEntity<List<Servico>> listar(@RequestParam(name = "id") Long idCliente){

        try{
            return ResponseEntity.status(HttpStatus.OK).body(service.listarTodos(idCliente));
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/Edit")
    public ResponseEntity<String> atualizar(@RequestBody @Valid ServicoDto dto
            , @RequestParam(name = "id") @NotBlank Long id){

        try{
            service.atualizarServico(dto.transform(), id);
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        catch(ObjetoInexistenteException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch(RuntimeException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/Delete")
    public ResponseEntity<String> remover(@RequestParam(name = "id") @NotBlank Long id){

        try{
            service.apagarServico(id);
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        catch(ObjetoInexistenteException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    //DEFINIR MAO DE OBRA - REGRA NEGOCIO
    @PutMapping("/MaoDeObra")
    public ResponseEntity<String> maoDeObra(@RequestBody ValoresServico maoDeObra, @RequestParam(name = "id") Long id){
        service.setMaoDeObra(maoDeObra.getValor(), id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    //-----------------------------------------------------------

    //RETORNA TODOS OS VALORES RELACIONADOS A REGRA NEGOCIO
    @GetMapping("/Valores")
    public ResponseEntity<ValoresServico> getvalores(@RequestParam(name = "id") Long id){
        return ResponseEntity.status(HttpStatus.OK).body(service.getValores(id));
    }
    //--------------------------------------------------------------------------------

    //DEFINIR DESCONTO - REGRA NEGOCIO
    @PutMapping("/Desconto")
    public ResponseEntity<String> desconto(@RequestParam(name = "id") Long id, @RequestBody DescontoDto desconto){
        service.aplicarDesconto(id, desconto.getPorcentagem());
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    //---------------------------------------------------------------------------------

    //DEFINIR ENTRADA - REGRA NEGOCIO
    @PutMapping("/Entrada/{id}")
    public ResponseEntity<String> entrada(@PathVariable("id") Long id, @RequestBody Entrada entrada){
        service.sendEntrada(entrada, id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    //----------------------------------------------------------------------------------

    //DEFINIR PAGAMENTO FINAL - REGRA NEGOCIO
    @PutMapping("/PagamentoFinal/{id}")
    public ResponseEntity<String> pagamentoFinal(@PathVariable("id") Long id, @RequestBody PagamentoFinal pagamentoFinal){
        service.sendFormaPagamentoFinal(pagamentoFinal, id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    //-----------------------------------------------------------------------------------

    //GERAR ORCAMENTO - REGRA NEGOCIO
    @PostMapping("/Orcamento")
    public ResponseEntity<String> sendOrcamento(@RequestParam("id") Long id, @RequestBody OrcamentoAdressTo adressTo){
        service.sendOrcamento(id, adressTo);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    //------------------------------------------------------------------------------------

    //ADD ETAPA - REGRA NEGOCIO
    @PostMapping("/Etapa")
    public ResponseEntity<String> addEtapa(@RequestParam("id") Long id, @RequestBody Etapa etapa){
        try {
            service.addEtapa(id, etapa);
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
    }
    //------------------------------------------------------------------------------------

    //REMOVE ETAPA - REGRA NEGOCIO
    @DeleteMapping("/Etapa/Delete")
    public ResponseEntity<String> removeEtapa(@RequestParam("id") Long id){
        try {
            etapaService.deleteEtapa(id);
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    //------------------------------------------------------------------------------------

}
