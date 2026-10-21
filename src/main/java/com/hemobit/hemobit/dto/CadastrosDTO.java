package com.hemobit.hemobit.dto;

import com.hemobit.hemobit.domain.TipoSanguineo;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public final class CadastrosDTO {
    private CadastrosDTO() {}

    public record NovoDoador(String nome, LocalDate dataNascimento, TipoSanguineo tipoSanguineo) {
        @Override public String toString() { return "NovoDoador[dados omitidos]"; }
    }
    public record DoadorResumo(Long id, String nome, TipoSanguineo tipoSanguineo) {
        @Override public String toString() { return "DoadorResumo[dados omitidos]"; }
    }
    public record DoadorOpcao(Long id, String nome, TipoSanguineo tipoSanguineo) {
        @Override public String toString() { return "DoadorOpcao[dados omitidos]"; }
    }
    public record NovaUnidade(String nome, String endereco, String cidade) {}
    public record UnidadeResumo(Long id, String nome, String endereco, String cidade) {}
    public record UnidadeOpcao(Long id, String nome) {}
    public record NovaDoacao(LocalDateTime dataHora, Double quantidade, Long doadorId, Long unidadeColetaId) {}
    public record DoacaoResumo(Long id, LocalDateTime dataHora, double quantidade,
                               Long doadorId, Long unidadeColetaId) {}
    public record OpcoesDoacao(List<DoadorOpcao> doadores, List<UnidadeOpcao> unidadesColeta) {}
}
