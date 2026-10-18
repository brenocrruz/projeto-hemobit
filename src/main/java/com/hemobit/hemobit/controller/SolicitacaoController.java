package com.hemobit.hemobit.controller;

import com.hemobit.hemobit.domain.Solicitacao;
import com.hemobit.hemobit.dto.HemocomponenteResponseDTO;
import com.hemobit.hemobit.dto.HistoricoSolicitacaoDTO;
import com.hemobit.hemobit.dto.MudancaStatusDTO;
import com.hemobit.hemobit.dto.SolicitacaoRequestDTO;
import com.hemobit.hemobit.dto.SolicitacaoResponseDTO;
import com.hemobit.hemobit.service.SolicitacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/solicitacoes")
public class SolicitacaoController {
    private final SolicitacaoService solicitacaoService;

    @Autowired
    public SolicitacaoController(SolicitacaoService solicitacaoService) {
        this.solicitacaoService = solicitacaoService;
    }

    @GetMapping
    public List<SolicitacaoResponseDTO> listarTodas() {
        return solicitacaoService.listarTodas().stream().map(SolicitacaoResponseDTO::new).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SolicitacaoResponseDTO> buscarPorId(@PathVariable Long id) {
        return solicitacaoService.buscarPorId(id)
                .map(s -> ResponseEntity.ok(new SolicitacaoResponseDTO(s)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<SolicitacaoResponseDTO> criar(@RequestBody SolicitacaoRequestDTO dto) {
        Solicitacao criada = solicitacaoService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new SolicitacaoResponseDTO(criada));
    }

    @PutMapping("/{id}")
    public SolicitacaoResponseDTO atualizar(@PathVariable Long id, @RequestBody SolicitacaoRequestDTO dto) {
        return new SolicitacaoResponseDTO(solicitacaoService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        solicitacaoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/status")
    public SolicitacaoResponseDTO alterarStatus(@PathVariable Long id, @RequestBody MudancaStatusDTO dto) {
        return new SolicitacaoResponseDTO(
                solicitacaoService.alterarStatus(id, dto.getStatus(), dto.getOperador()));
    }

    @PutMapping("/{id}/atender")
    public SolicitacaoResponseDTO atender(@PathVariable Long id,
                                          @RequestParam(required = false) String operador) {
        return new SolicitacaoResponseDTO(solicitacaoService.atender(id, operador));
    }

    @GetMapping("/{id}/historico")
    public List<HistoricoSolicitacaoDTO> historico(@PathVariable Long id) {
        return solicitacaoService.listarHistorico(id).stream().map(HistoricoSolicitacaoDTO::new).toList();
    }

    @GetMapping("/{id}/bolsas")
    public List<HemocomponenteResponseDTO> bolsasReservadas(@PathVariable Long id) {
        return solicitacaoService.listarBolsasReservadas(id).stream()
                .map(HemocomponenteResponseDTO::new).toList();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> tratarDadoInvalido(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> tratarNaoEncontrado(NoSuchElementException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> tratarConflito(IllegalStateException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }
}
