package com.hemobit.hemobit.controller;

import com.hemobit.hemobit.dto.HemocomponenteRequestDTO;
import com.hemobit.hemobit.dto.HemocomponenteResponseDTO;
import com.hemobit.hemobit.service.HemocomponenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hemocomponentes")
public class HemocomponenteController {

    private final HemocomponenteService hemocomponenteService;

    @Autowired
    public HemocomponenteController(HemocomponenteService hemocomponenteService) {
        this.hemocomponenteService = hemocomponenteService;
    }

    @GetMapping
    public List<HemocomponenteResponseDTO> listarTodos() {
        return hemocomponenteService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<HemocomponenteResponseDTO> buscarPorId(@PathVariable Long id) {
        return hemocomponenteService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody HemocomponenteRequestDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(hemocomponenteService.criar(dto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        hemocomponenteService.deletar(id);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody HemocomponenteRequestDTO dto) {
        try {
            return ResponseEntity.ok(hemocomponenteService.atualizar(id, dto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}