package com.hemobit.hemobit.controller;

import com.hemobit.hemobit.domain.Localidade;
import com.hemobit.hemobit.domain.Rota;
import com.hemobit.hemobit.dto.RotaRequestDTO;
import com.hemobit.hemobit.dto.RotaResponseDTO;
import com.hemobit.hemobit.repository.LocalidadeRepository;
import com.hemobit.hemobit.service.RotaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/rotas")
public class RotaApiController {
    private final RotaService rotaService;
    private final LocalidadeRepository localidadeRepository;

    public RotaApiController(RotaService rotaService, LocalidadeRepository localidadeRepository) {
        this.rotaService = rotaService;
        this.localidadeRepository = localidadeRepository;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public List<RotaResponseDTO> listar() {
        return rotaService.listarTodas().stream()
                .map(RotaResponseDTO::new)
                .toList();
    }

    @PostMapping
    @Transactional
    public ResponseEntity<RotaResponseDTO> criar(@RequestBody RotaRequestDTO dto) {
        Localidade origem = buscarLocalidade(dto.getOrigemId(), "origem");
        Localidade destino = buscarLocalidade(dto.getDestinoId(), "destino");
        if (dto.getTempoEstimado() <= 0) {
            throw new IllegalArgumentException("O tempo estimado deve ser maior que zero.");
        }
        Rota salva = rotaService.salvar(new Rota(origem, destino, dto.getDistancia(), dto.getTempoEstimado()));
        return ResponseEntity.status(HttpStatus.CREATED).body(new RotaResponseDTO(salva));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> tratarDadoInvalido(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    private Localidade buscarLocalidade(Long id, String papel) {
        if (id == null) {
            throw new IllegalArgumentException("Informe a localidade de " + papel + ".");
        }
        return localidadeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Localidade de " + papel + " não encontrada."));
    }
}
