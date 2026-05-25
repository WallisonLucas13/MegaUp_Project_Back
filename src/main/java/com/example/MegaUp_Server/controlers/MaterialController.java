package com.example.MegaUp_Server.controlers;

import com.example.MegaUp_Server.dtos.MaterialDto;
import com.example.MegaUp_Server.models.Material;
import com.example.MegaUp_Server.services.MaterialService;
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
@RequestMapping("/materiais")
@RequiredArgsConstructor
@Log4j2
public class MaterialController {

    private final MaterialService service;

    @PostMapping("/servico/{servicoId}")
    public ResponseEntity<String> save(@RequestBody @Valid MaterialDto dto,
            @PathVariable(name = "servicoId") Long servicoId) {
        log.info("POST /materiais/servico/{} - adicionando material [nome={}]", servicoId, dto.nome());
        this.service.salvarMaterial(dto.transform(), servicoId);
        log.info("POST /materiais/servico/{} - material adicionado com sucesso [nome={}]", servicoId, dto.nome());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/servico/{servicoId}")
    public ResponseEntity<List<Material>> listar(@PathVariable(name = "servicoId") Long servicoId) {
        log.info("GET /materiais/servico/{} - listando materiais", servicoId);
        List<Material> result = service.listarTodos(servicoId);
        log.info("GET /materiais/servico/{} - {} material(is) retornado(s)", servicoId, result.size());
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> atualizar(@RequestBody @Valid MaterialDto dto,
            @PathVariable(name = "id") @NotNull Long id) {
        log.info("PUT /materiais/{} - atualizando material [nome={}]", id, dto.nome());
        service.atualizarMaterial(dto.transform(), id);
        log.info("PUT /materiais/{} - material atualizado com sucesso", id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> remover(@PathVariable(name = "id") @NotNull Long id) {
        log.info("DELETE /materiais/{} - removendo material", id);
        service.apagarMaterial(id);
        log.info("DELETE /materiais/{} - material removido com sucesso", id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
