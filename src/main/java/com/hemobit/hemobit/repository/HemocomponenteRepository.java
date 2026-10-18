package com.hemobit.hemobit.repository;

import com.hemobit.hemobit.domain.Hemocomponente;
import com.hemobit.hemobit.domain.StatusHemocomponente;
import com.hemobit.hemobit.domain.TipoHemocomponente;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface HemocomponenteRepository extends JpaRepository<Hemocomponente, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select h from Hemocomponente h "
            + "where h.tipo = :tipo and h.status = :status and h.dataValidade >= :hoje "
            + "order by h.dataValidade asc, h.id asc")
    List<Hemocomponente> buscarParaReserva(@Param("tipo") TipoHemocomponente tipo,
                                           @Param("status") StatusHemocomponente status,
                                           @Param("hoje") LocalDate hoje);

    List<Hemocomponente> findBySolicitacaoIdOrderByDataValidadeAscIdAsc(Long solicitacaoId);
}
