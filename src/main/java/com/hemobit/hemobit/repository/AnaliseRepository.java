package com.hemobit.hemobit.repository;

import com.hemobit.hemobit.domain.Hemocomponente;
import com.hemobit.hemobit.domain.StatusHemocomponente;
import com.hemobit.hemobit.domain.StatusSolicitacao;
import com.hemobit.hemobit.domain.TipoHemocomponente;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface AnaliseRepository extends Repository<Hemocomponente, Long> {

    @Query("select count(h) from Hemocomponente h "
            + "where h.tipo = :tipo and h.status = :status and h.dataValidade >= :hoje")
    long contarEstoqueValido(@Param("tipo") TipoHemocomponente tipo,
                             @Param("status") StatusHemocomponente status,
                             @Param("hoje") LocalDate hoje);

    @Query("select count(h) from Hemocomponente h where h.tipo = :tipo")
    long contarTotal(@Param("tipo") TipoHemocomponente tipo);

    @Query("select count(h) from Hemocomponente h "
            + "where h.tipo = :tipo and (h.status in :descartados "
            + "or (h.status = :disponivel and h.dataValidade < :hoje))")
    long contarVencidas(@Param("tipo") TipoHemocomponente tipo,
                        @Param("descartados") List<StatusHemocomponente> descartados,
                        @Param("disponivel") StatusHemocomponente disponivel,
                        @Param("hoje") LocalDate hoje);

    @Query("select coalesce(sum(s.quantidade), 0.0) from Solicitacao s "
            + "where s.tipoHemocomponente = :tipo and s.dataHora >= :desde "
            + "and s.status <> :excluida")
    double somarDemanda(@Param("tipo") TipoHemocomponente tipo,
                        @Param("desde") LocalDateTime desde,
                        @Param("excluida") StatusSolicitacao excluida);
}