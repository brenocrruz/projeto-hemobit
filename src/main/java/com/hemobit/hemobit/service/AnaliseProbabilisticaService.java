package com.hemobit.hemobit.service;

import com.hemobit.hemobit.domain.StatusHemocomponente;
import com.hemobit.hemobit.domain.StatusSolicitacao;
import com.hemobit.hemobit.domain.TipoHemocomponente;
import com.hemobit.hemobit.dto.AnaliseTipoDTO;
import com.hemobit.hemobit.repository.AnaliseRepository;
import com.hemobit.hemobit.util.EstatisticaUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AnaliseProbabilisticaService {

    /** Janela do histórico usada para estimar a demanda diária. */
    public static final int JANELA_DEMANDA_DIAS = 30;
    /** Quantos dias o estoque precisa aguentar até a reposição. */
    public static final int HORIZONTE_DIAS = 3;

    private final AnaliseRepository repository;

    public AnaliseProbabilisticaService(AnaliseRepository repository) {
        this.repository = repository;
    }

    public List<AnaliseTipoDTO> analisarPorTipo() {
        LocalDate hoje = LocalDate.now();
        LocalDateTime desde = hoje.minusDays(JANELA_DEMANDA_DIAS).atStartOfDay();
        List<AnaliseTipoDTO> resultado = new ArrayList<>();

        for (TipoHemocomponente tipo : TipoHemocomponente.values()) {
            long estoque = repository.contarEstoqueValido(
                    tipo, StatusHemocomponente.DISPONIVEL, hoje);

            double demandaTotal = repository.somarDemanda(
                    tipo, desde, StatusSolicitacao.CANCELADA);
            double demandaDiaria = demandaTotal / JANELA_DEMANDA_DIAS;
            double lambda = EstatisticaUtil.lambdaHorizonte(
                    demandaTotal, JANELA_DEMANDA_DIAS, HORIZONTE_DIAS);
            double prob = EstatisticaUtil.probabilidadeDesabastecimento(lambda, (int) estoque);

            long total = repository.contarTotal(tipo);
            long vencidas = repository.contarVencidas(
                    tipo,
                    List.of(StatusHemocomponente.VENCIDO, StatusHemocomponente.DESCARTADO),
                    StatusHemocomponente.DISPONIVEL,
                    hoje);
            double taxa = EstatisticaUtil.taxaDescarte(vencidas, total);

            resultado.add(new AnaliseTipoDTO(
                    tipo.name(), estoque, demandaDiaria, lambda,
                    prob * 100.0, classificarRisco(prob),
                    total, vencidas, taxa));
        }
        return resultado;
    }

    static String classificarRisco(double probabilidade) {
        if (probabilidade >= 0.5) {
            return "ALTO";
        }
        if (probabilidade >= 0.2) {
            return "MEDIO";
        }
        return "BAIXO";
    }
}