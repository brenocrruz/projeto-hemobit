package com.hemobit.hemobit.controller;

import com.hemobit.hemobit.dto.AnaliseTipoDTO;
import com.hemobit.hemobit.service.AnaliseProbabilisticaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/analise")
@CrossOrigin(origins = "*")
public class AnaliseProbabilisticaController {

    private final AnaliseProbabilisticaService service;

    public AnaliseProbabilisticaController(AnaliseProbabilisticaService service) {
        this.service = service;
    }

    @GetMapping("/probabilistica")
    public ResponseEntity<List<AnaliseTipoDTO>> getAnalise() {
        return ResponseEntity.ok(service.analisarPorTipo());
    }
}