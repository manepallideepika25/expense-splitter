package com.deepika.expense_splitter.expense;

import java.math.BigDecimal;

public record SplitInput(
        Long userId,
        BigDecimal value
)
{}
