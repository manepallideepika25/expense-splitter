package com.deepika.expense_splitter.expense;

import com.deepika.expense_splitter.exception.BadRequestException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ExpenseSplitCalculatorTest {

    private final ExpenseSplitCalculator calculator = new ExpenseSplitCalculator();

    private BigDecimal sum(Map<Long, BigDecimal> shares) {
        return shares.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Test
    void equalSplit_dividesEvenly() {
        var shares = calculator.calculate(new BigDecimal("4000"), SplitType.EQUAL,
                List.of(new SplitInput(1L, null), new SplitInput(2L, null),
                        new SplitInput(3L, null), new SplitInput(4L, null)));

        assertEquals(new BigDecimal("1000.00"), shares.get(1L));
        assertEquals(new BigDecimal("1000.00"), shares.get(4L));
    }

    @Test
    void equalSplit_handlesRemainderWithoutLosingPaise() {
        var shares = calculator.calculate(new BigDecimal("100"), SplitType.EQUAL,
                List.of(new SplitInput(1L, null), new SplitInput(2L, null),
                        new SplitInput(3L, null)));

        assertEquals(new BigDecimal("33.34"), shares.get(1L));
        assertEquals(new BigDecimal("33.33"), shares.get(2L));
        assertEquals(new BigDecimal("33.33"), shares.get(3L));
        assertEquals(new BigDecimal("100.00"), sum(shares));
    }

    @Test
    void exactSplit_acceptsAmountsThatMatchTotal() {
        var shares = calculator.calculate(new BigDecimal("1000"), SplitType.EXACT,
                List.of(new SplitInput(1L, new BigDecimal("600")),
                        new SplitInput(2L, new BigDecimal("400"))));

        assertEquals(new BigDecimal("600.00"), shares.get(1L));
        assertEquals(new BigDecimal("400.00"), shares.get(2L));
    }

    @Test
    void exactSplit_rejectsWrongSum() {
        assertThrows(BadRequestException.class, () ->
                calculator.calculate(new BigDecimal("1000"), SplitType.EXACT,
                        List.of(new SplitInput(1L, new BigDecimal("600")),
                                new SplitInput(2L, new BigDecimal("300")))));
    }

    @Test
    void percentageSplit_calculatesShares() {
        var shares = calculator.calculate(new BigDecimal("1000"), SplitType.PERCENTAGE,
                List.of(new SplitInput(1L, new BigDecimal("50")),
                        new SplitInput(2L, new BigDecimal("30")),
                        new SplitInput(3L, new BigDecimal("20"))));

        assertEquals(new BigDecimal("500.00"), shares.get(1L));
        assertEquals(new BigDecimal("300.00"), shares.get(2L));
        assertEquals(new BigDecimal("200.00"), shares.get(3L));
    }

    @Test
    void percentageSplit_alwaysAddsUpToTotal() {
        var shares = calculator.calculate(new BigDecimal("100"), SplitType.PERCENTAGE,
                List.of(new SplitInput(1L, new BigDecimal("33.33")),
                        new SplitInput(2L, new BigDecimal("33.33")),
                        new SplitInput(3L, new BigDecimal("33.34"))));

        assertEquals(new BigDecimal("100.00"), sum(shares));
    }

    @Test
    void percentageSplit_rejectsWhenNotHundred() {
        assertThrows(BadRequestException.class, () ->
                calculator.calculate(new BigDecimal("1000"), SplitType.PERCENTAGE,
                        List.of(new SplitInput(1L, new BigDecimal("50")),
                                new SplitInput(2L, new BigDecimal("30")))));
    }

    @Test
    void rejectsDuplicateParticipants() {
        assertThrows(BadRequestException.class, () ->
                calculator.calculate(new BigDecimal("100"), SplitType.EQUAL,
                        List.of(new SplitInput(1L, null), new SplitInput(1L, null))));
    }

    @Test
    void rejectsZeroAmount() {
        assertThrows(BadRequestException.class, () ->
                calculator.calculate(BigDecimal.ZERO, SplitType.EQUAL,
                        List.of(new SplitInput(1L, null))));
    }
}