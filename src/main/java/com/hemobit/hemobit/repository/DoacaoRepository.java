package com.hemobit.hemobit.repository;

import com.hemobit.hemobit.domain.Doacao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoacaoRepository extends JpaRepository<Doacao, Long> {
}