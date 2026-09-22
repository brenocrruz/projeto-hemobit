package com.hemobit.hemobit.controller;

import com.hemobit.hemobit.domain.ProfissionalSolicitante;
import com.hemobit.hemobit.service.ProfissionalSolicitanteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/profissionais")
public class ProfissionalSolicitanteController {

    private final ProfissionalSolicitanteService service;

    public ProfissionalSolicitanteController(ProfissionalSolicitanteService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ProfissionalSolicitante> cadastrar(
            @RequestBody ProfissionalSolicitante profissional) {

        ProfissionalSolicitante profissionalSalvo =
                service.cadastrar(profissional);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(profissionalSalvo);
    }

    @GetMapping
    public List<ProfissionalSolicitante> listarTodos(){
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfissionalSolicitante> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(
            @PathVariable Long id,
            @RequestBody ProfissionalSolicitante profissional) {

        try {
            return ResponseEntity.ok(service.atualizar(id, profissional));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {

        try {
            service.deletar(id);
            return ResponseEntity.noContent().build();

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }



}