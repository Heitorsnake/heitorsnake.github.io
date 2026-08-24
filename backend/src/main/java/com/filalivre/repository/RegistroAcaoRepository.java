package com.filalivre.repository;

import com.filalivre.model.RegistroAcao;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistroAcaoRepository extends JpaRepository<RegistroAcao, Long> {

    List<RegistroAcao> findTop100ByOrderByMomentoDesc();
}
