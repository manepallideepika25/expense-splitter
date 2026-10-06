package com.deepika.expense_splitter.settlement;

import java.math.BigDecimal;

public record SuggestedSettlement(
        Long fromUserId,
        String fromName,
        Long toUserId,
        String toName,
        BigDecimal amount
) {
}
