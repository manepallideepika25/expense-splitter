package com.deepika.expense_splitter.expense;

import java.math.BigDecimal;

public record UserAmount(Long userId, BigDecimal total) {
}
