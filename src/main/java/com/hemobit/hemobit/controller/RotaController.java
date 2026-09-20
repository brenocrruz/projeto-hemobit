package com.hemobit.hemobit.controller;

import com.hemobit.hemobit.domain.Rota;
import com.hemobit.hemobit.service.RotaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rotas")
public class RotaController {

    private final RotaService rotaService;

    @Autowired
    public RotaController(RotaService rotaService) {
        this.rotaService = rotaService;
    }

    @GetMapping
    public List<Rota> listarTodas() {
        return rotaService.listarTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Rota> buscarPorId(@PathVariable Long id) {
        return rotaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Rota rota) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(rotaService.salvar(rota));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody Rota rota) {
        try {
            return ResponseEntity.ok(rotaService.atualizar(id, rota));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        rotaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}