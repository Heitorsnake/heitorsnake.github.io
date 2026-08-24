package com.filalivre.repository;

import com.filalivre.model.Solicitacao;
import com.filalivre.model.StatusSolicitacao;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SolicitacaoRepository extends JpaRepository<Solicitacao, Long> {

    List<Solicitacao> findByStatusOrderByCriadoEmDesc(StatusSolicitacao status);

    List<Solicitacao> findByOperadorIdOrderByCriadoEmDesc(Long operadorId);

    List<Solicitacao> findTop100ByOrderByCriadoEmDesc();

    List<Solicitacao> findByStatusIn(Collection<StatusSolicitacao> statuses);

    boolean existsByCaixaIdAndStatus(Long caixaId, StatusSolicitacao status);
}
