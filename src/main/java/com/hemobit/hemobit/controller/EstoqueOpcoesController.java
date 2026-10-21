package com.hemobit.hemobit.controller;

import com.hemobit.hemobit.dto.EstoqueOpcoesDTO;
import com.hemobit.hemobit.repository.DoacaoRepository;
import com.hemobit.hemobit.repository.LocalidadeRepository;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EstoqueOpcoesController {
    private final DoacaoRepository doacoes;
    private final LocalidadeRepository localidades;

    public EstoqueOpcoesController(DoacaoRepository doacoes, LocalidadeRepository localidades) {
        this.doacoes = doacoes;
        this.localidades = localidades;
    }

    @GetMapping("/api/estoque/opcoes")
    @Transactional(readOnly = true)
    public EstoqueOpcoesDTO listar() {
        var opcoesDoacoes = doacoes.findAll(Sort.by("id")).stream()
                .map(d -> new EstoqueOpcoesDTO.DoacaoOpcao(d.getId(), d.getDataHora(),
                        d.getDoador() == null ? null : d.getDoador().getTipoSanguineo()))
                .toList();
        var opcoesLocalidades = localidades.findAll(Sort.by("nome", "id")).stream()
                .map(l -> new EstoqueOpcoesDTO.LocalidadeOpcao(l.getId(), l.getNome()))
                .toList();
        return new EstoqueOpcoesDTO(opcoesDoacoes, opcoesLocalidades);
    }
}
