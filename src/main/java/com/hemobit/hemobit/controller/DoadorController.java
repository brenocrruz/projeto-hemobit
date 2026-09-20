package com.hemobit.hemobit.controller;

import com.hemobit.hemobit.domain.Doador;
import com.hemobit.hemobit.service.DoadorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/doadores")
public class DoadorController {

    private final DoadorService doadorService;

    @Autowired
    public DoadorController(DoadorService doadorService) {
        this.doadorService = doadorService;
    }

    @GetMapping
    public List<Doador> listarTodos() {
        return doadorService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Doador> buscarPorId(@PathVariable Long id) {
        return doadorService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Doador criar(@RequestBody Doador doador) {
        return doadorService.salvar(doador);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        doadorService.deletar(id);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody Doador doador) {
        try {
            return ResponseEntity.ok(doadorService.atualizar(id, doador));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
}
}