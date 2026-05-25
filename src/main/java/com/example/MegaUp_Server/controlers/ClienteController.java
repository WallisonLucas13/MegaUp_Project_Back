package com.example.MegaUp_Server.controlers;

import com.example.MegaUp_Server.dtos.ClienteDto;
import com.example.MegaUp_Server.dtos.ClienteResponseDto;
import com.example.MegaUp_Server.services.ClienteService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
@Log4j2
public class ClienteController {

    private final ClienteService service;

    @PostMapping
    public ResponseEntity<String> save(@RequestBody @Valid ClienteDto dto) {
        log.info("POST /clientes - criando cliente [nome={}]", dto.nome());
        this.service.salvarCliente(dto.transform());
        log.info("POST /clientes - cliente criado com sucesso [nome={}]", dto.nome());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<List<ClienteResponseDto>> listar() {
        log.info("GET /clientes - listando clientes");
        List<ClienteResponseDto> result = service.listarTodos().stream()
                .map(ClienteResponseDto::from)
                .collect(Collectors.toList());
        log.info("GET /clientes - {} cliente(s) retornado(s)", result.size());
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> atualizar(@RequestBody @Valid ClienteDto dto,
            @PathVariable(name = "id") @NotNull Long id) {
        log.info("PUT /clientes/{} - atualizando cliente [nome={}]", id, dto.nome());
        service.atualizarCliente(dto.transform(), id);
        log.info("PUT /clientes/{} - cliente atualizado com sucesso", id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> remover(@PathVariable(name = "id") @NotNull Long id) {
        log.info("DELETE /clientes/{} - removendo cliente", id);
        service.apagarCliente(id);
        log.info("DELETE /clientes/{} - cliente removido com sucesso", id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
