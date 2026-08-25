package com.filalivre.dto;

import com.filalivre.model.TipoSolicitacao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record SolicitacaoRequest(
        @NotNull Long caixaId,
        @NotNull TipoSolicitacao tipo,
        @NotBlank String produto,
        @NotNull @Positive Integer quantidade,
        @NotNull @Positive BigDecimal valor,
        String motivo) {
}
