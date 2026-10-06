package com.deepika.expense_splitter.balance;

import java.math.BigDecimal;

public record MemberBalance(
        Long userId,
        String name,
        BigDecimal totalPaid,
        BigDecimal totalShare,
        BigDecimal netBalance
) {
}
