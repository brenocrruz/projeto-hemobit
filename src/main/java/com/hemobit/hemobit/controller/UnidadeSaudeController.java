package com.hemobit.hemobit.controller;

import com.hemobit.hemobit.domain.UnidadeSaude;
import com.hemobit.hemobit.service.UnidadeSaudeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/unidades-saude")
public class UnidadeSaudeController {

    private final UnidadeSaudeService unidadeSaudeService;

    @Autowired
    public UnidadeSaudeController(UnidadeSaudeService unidadeSaudeService) {
        this.unidadeSaudeService = unidadeSaudeService;
    }

    @GetMapping
    public List<UnidadeSaude> listarTodas() {
        return unidadeSaudeService.listarTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UnidadeSaude> buscarPorId(@PathVariable Long id) {
        return unidadeSaudeService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody UnidadeSaude unidadeSaude) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(unidadeSaudeService.salvar(unidadeSaude));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody UnidadeSaude unidadeSaude) {
        try {
            return ResponseEntity.ok(unidadeSaudeService.atualizar(id, unidadeSaude));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        unidadeSaudeService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}