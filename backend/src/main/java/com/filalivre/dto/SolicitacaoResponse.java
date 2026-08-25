package com.filalivre.dto;

import com.filalivre.model.StatusSolicitacao;
import com.filalivre.model.TipoSolicitacao;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SolicitacaoResponse(
        Long id,
        Long caixaId,
        Integer caixaNumero,
        String operadorNome,
        TipoSolicitacao tipo,
        String produto,
        Integer quantidade,
        BigDecimal valor,
        String motivo,
        StatusSolicitacao status,
        LocalDateTime criadoEm,
        LocalDateTime decididoEm,
        String decididoPorNome) {
}
