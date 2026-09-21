package com.hemobit.hemobit.repository;

import com.hemobit.hemobit.domain.ProfissionalSolicitante;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfissionalSolicitanteRepository
        extends JpaRepository<ProfissionalSolicitante, Long> {

    boolean existsByCrmMatricula(String crmMatricula);
}