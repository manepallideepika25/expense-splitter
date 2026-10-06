package com.deepika.expense_splitter.settlement;

import java.math.BigDecimal;

public record Transfer(
        Long fromUserId,
        Long toUserId,
        BigDecimal amount
) {
}
