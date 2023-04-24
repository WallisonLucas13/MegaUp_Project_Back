package com.example.MegaUp_Server.controlers;

import com.example.MegaUp_Server.dtos.MaterialDto;
import com.example.MegaUp_Server.exceptions.ObjetoInexistenteException;
import com.example.MegaUp_Server.models.Material;
import com.example.MegaUp_Server.services.MaterialService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Material")
@CrossOrigin("*")
public class MaterialController {

    @Autowired
    private MaterialService service;

    @PostMapping("/New")
    public ResponseEntity<String> save(@RequestBody @Valid MaterialDto dto, @RequestParam(name = "id") Long id){

        try {
            this.service.salvarMaterial(dto.transform(), id);
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        catch(RuntimeException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/Todos")
    public ResponseEntity<List<Material>> listar(@RequestParam(name = "id") Long id){

        try{
            return ResponseEntity.status(HttpStatus.OK).body(service.listarTodos(id));
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/Edit")
    public ResponseEntity<String> atualizar(@RequestBody @Valid MaterialDto dto
            , @RequestParam(name = "id") @NotBlank Long id){

        try{
            service.atualizarMaterial(dto.transform(), id);
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
            service.apagarMaterial(id);
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        catch(ObjetoInexistenteException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
