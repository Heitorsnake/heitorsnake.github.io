package com.filalivre.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CaixaRequest(@NotNull @Positive Integer numero) {
}
