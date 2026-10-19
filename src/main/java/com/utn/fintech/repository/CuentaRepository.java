package com.utn.fintech.repository;

import com.utn.fintech.model.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CuentaRepository extends JpaRepository<Cuenta, Long> {

    boolean existsByNumero(String numero);
}
