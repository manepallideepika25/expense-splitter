package com.deepika.expense_splitter.expense.dto;

import com.deepika.expense_splitter.expense.SplitType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record CreateExpenseRequest(
        @NotBlank(message = "Description is required")
        String description,

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be at least 0.01")
        BigDecimal amount,

        @NotNull(message = "paidByUserId is required")
        Long paidByUserId,

        @NotNull(message = "splitType is required")
        SplitType splitType,

        LocalDate expenseDate,

        @NotEmpty(message = "At least one participant is required")
        @Valid
        List<ParticipantRequest> participants
) {
}
