package com.deepika.expense_splitter.expense.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ParticipantRequest(
        @NotNull(message = "userId is required")
        Long userId,

        BigDecimal value
)
{}
