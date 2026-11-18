package com.hemobit.hemobit.controller;

import com.hemobit.hemobit.domain.StatusHemocomponente;
import com.hemobit.hemobit.domain.TipoHemocomponente;
import com.hemobit.hemobit.domain.TipoSanguineo;
import com.hemobit.hemobit.dto.HemocomponenteRequestDTO;
import com.hemobit.hemobit.dto.HemocomponenteResponseDTO;
import com.hemobit.hemobit.dto.TipoHemocomponenteDTO;
import com.hemobit.hemobit.service.HemocomponenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/hemocomponentes")
public class HemocomponenteController {

    private final HemocomponenteService hemocomponenteService;

    @Autowired
    public HemocomponenteController(HemocomponenteService hemocomponenteService) {
        this.hemocomponenteService = hemocomponenteService;
    }

    @GetMapping
    public List<HemocomponenteResponseDTO> listar(
            @RequestParam(required = false) TipoSanguineo tipoSanguineo,
            @RequestParam(required = false) StatusHemocomponente status,
            @RequestParam(required = false) TipoHemocomponente tipo) {
        return hemocomponenteService.listar(tipoSanguineo, status, tipo);
    }

    @GetMapping("/tipos")
    public List<TipoHemocomponenteDTO> listarTipos() {
        return hemocomponenteService.listarTipos();
    }

    @GetMapping("/estoque")
    public Map<TipoSanguineo, Long> resumoEstoque() {
        return hemocomponenteService.resumoEstoque();
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

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> tratarCorpoInvalido(HttpMessageNotReadableException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                "Corpo da requisição inválido: confira o tipo do hemocomponente, o tipo sanguíneo "
                        + "(A_POSITIVO, A_NEGATIVO, B_POSITIVO, B_NEGATIVO, AB_POSITIVO, AB_NEGATIVO, "
                        + "O_POSITIVO, O_NEGATIVO) e as datas (AAAA-MM-DD).");
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> tratarConflito(IllegalStateException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
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