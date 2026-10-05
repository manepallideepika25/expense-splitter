package com.deepika.expense_splitter.expense.dto;

import java.math.BigDecimal;

public record SplitResponse(
        Long userId, String name, BigDecimal shareAmount
) {
}
