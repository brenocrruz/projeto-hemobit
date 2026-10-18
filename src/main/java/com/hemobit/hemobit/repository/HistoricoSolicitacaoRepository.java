package com.hemobit.hemobit.repository;

import com.hemobit.hemobit.domain.HistoricoSolicitacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoricoSolicitacaoRepository extends JpaRepository<HistoricoSolicitacao, Long> {

    List<HistoricoSolicitacao> findBySolicitacaoIdOrderByDataHoraAscIdAsc(Long solicitacaoId);

    void deleteBySolicitacaoId(Long solicitacaoId);
}
