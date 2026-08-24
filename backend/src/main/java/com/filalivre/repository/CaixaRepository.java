package com.filalivre.repository;

import com.filalivre.model.Caixa;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CaixaRepository extends JpaRepository<Caixa, Long> {

    List<Caixa> findAllByOrderByNumeroAsc();

    Optional<Caixa> findByNumero(Integer numero);

    boolean existsByNumero(Integer numero);
}
