package com.filalivre.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record TotaisRequest(
        @NotNull @PositiveOrZero java.math.BigDecimal valorCompra,
        @NotNull @PositiveOrZero Integer qtdItens) {
}
