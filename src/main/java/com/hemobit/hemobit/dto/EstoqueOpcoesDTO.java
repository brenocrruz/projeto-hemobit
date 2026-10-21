package com.hemobit.hemobit.dto;

import com.hemobit.hemobit.domain.TipoSanguineo;
import java.time.LocalDateTime;
import java.util.List;

/** Dados mínimos para cadastro e identificação do estoque, sem dados pessoais do doador. */
public record EstoqueOpcoesDTO(List<DoacaoOpcao> doacoes, List<LocalidadeOpcao> localidades) {
    public record DoacaoOpcao(Long id, LocalDateTime dataHora, TipoSanguineo tipoSanguineo) {}
    public record LocalidadeOpcao(Long id, String nome) {}
}
