package com.hemobit.hemobit.aed;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hemobit.hemobit.aed.estruturas.Fila;

@RestController
@RequestMapping("/aed/solicitacoes")
public class SolicitacaoDemoController {

    private Fila<String> filaSolicitacoes = new Fila<>();

    @GetMapping
    public ResponseEntity<Object> listarSolicitacoes() {
        return ResponseEntity.status(HttpStatus.OK).body("Fila atual de solicitações.");
    }

    @PostMapping
    public ResponseEntity<Object> criarSolicitacao(@RequestBody String dadosSolicitacao) {
        if (dadosSolicitacao == null || dadosSolicitacao.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Solicitação inválida.");
        }
        filaSolicitacoes.enfileirar(dadosSolicitacao);
        return ResponseEntity.status(HttpStatus.CREATED).body("Solicitação inserida na fila.");
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Object> atualizarStatus(@PathVariable Long id, @RequestBody String novoStatus) {
        return ResponseEntity.status(HttpStatus.OK).body("Status alterado para: " + novoStatus);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelarSolicitacao(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
