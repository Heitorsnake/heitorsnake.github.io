package com.filalivre.dto;

import java.time.LocalDateTime;

public record RegistroAcaoResponse(
        Long id,
        String usuarioNome,
        Integer caixaNumero,
        String acao,
        String detalhes,
        LocalDateTime momento) {
}
