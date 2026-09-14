package com.hemobit.hemobit.repository;

import com.hemobit.hemobit.domain.Doador;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoadorRepository extends JpaRepository<Doador, Long> {
}