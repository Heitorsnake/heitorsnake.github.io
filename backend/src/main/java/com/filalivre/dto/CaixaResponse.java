package com.filalivre.dto;

import com.filalivre.model.StatusCaixa;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CaixaResponse(
        Long id,
        Integer numero,
        StatusCaixa status,
        BigDecimal valorCompra,
        Integer qtdItens,
        String operadorNome,
        LocalDateTime inicioAtendimento,
        boolean solicitacaoPendente) {
}
