package com.deepika.expense_splitter.settlement.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SettlementResponse(
        Long id,
        Long groupId,
        Long fromUserId,
        String fromName,
        Long toUserId,
        String toName,
        BigDecimal amount,
        LocalDateTime settledAt
) {
}
