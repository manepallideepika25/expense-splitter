package com.deepika.expense_splitter.expense.dto;

import com.deepika.expense_splitter.expense.SplitType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ExpenseResponse(
        Long id,
        Long groupId,
        String description,
        BigDecimal amount,
        Long paidByUserId,
        String paidByName,
        SplitType splitType,
        LocalDate expenseDate,
        List<SplitResponse> splits
) {
}
