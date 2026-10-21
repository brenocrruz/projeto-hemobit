package com.hemobit.hemobit.controller;

import com.hemobit.hemobit.dto.DashboardContagensDTO;
import com.hemobit.hemobit.repository.DoadorRepository;
import com.hemobit.hemobit.repository.DoacaoRepository;
import com.hemobit.hemobit.repository.UnidadeColetaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DashboardContagensController {
    private final DoadorRepository doadores;
    private final DoacaoRepository doacoes;
    private final UnidadeColetaRepository unidadesColeta;

    public DashboardContagensController(DoadorRepository doadores, DoacaoRepository doacoes,
                                         UnidadeColetaRepository unidadesColeta) {
        this.doadores = doadores;
        this.doacoes = doacoes;
        this.unidadesColeta = unidadesColeta;
    }

    @GetMapping("/api/dashboard/contagens")
    @Transactional(readOnly = true)
    public DashboardContagensDTO contagens() {
        return new DashboardContagensDTO(doadores.count(), doacoes.count(), unidadesColeta.count());
    }
}
