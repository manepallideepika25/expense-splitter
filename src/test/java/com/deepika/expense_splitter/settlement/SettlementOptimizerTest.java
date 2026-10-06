package com.deepika.expense_splitter.settlement;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SettlementOptimizerTest {

    private final SettlementOptimizer optimizer = new SettlementOptimizer();

    private BigDecimal bd(String value) {
        return new BigDecimal(value);
    }

    /** Applying every transfer must bring every balance to exactly zero. */
    private void assertSettlesEveryone(Map<Long, BigDecimal> balances, List<Transfer> transfers) {
        Map<Long, BigDecimal> remaining = new HashMap<>(balances);
        for (Transfer t : transfers) {
            remaining.merge(t.fromUserId(), t.amount(), BigDecimal::add);
            remaining.merge(t.toUserId(), t.amount().negate(), BigDecimal::add);
        }
        remaining.values().forEach(v -> assertEquals(0, v.signum()));
    }

    @Test
    void oneCreditorManyDebtors() {
        Map<Long, BigDecimal> balances = Map.of(
                1L, bd("3000.00"), 2L, bd("-1000.00"),
                3L, bd("-1500.00"), 4L, bd("-500.00"));

        List<Transfer> transfers = optimizer.optimize(balances);

        assertEquals(3, transfers.size());
        assertTrue(transfers.stream().allMatch(t -> t.toUserId().equals(1L)));
        assertSettlesEveryone(balances, transfers);
    }

    @Test
    void chainOfDebtsCollapsesToOnePayment() {
        // A owes B 50 and B owes C 50, so B drops out and A pays C directly.
        Map<Long, BigDecimal> balances = Map.of(
                1L, bd("-50.00"), 2L, bd("0.00"), 3L, bd("50.00"));

        List<Transfer> transfers = optimizer.optimize(balances);

        assertEquals(1, transfers.size());
        assertEquals(new Transfer(1L, 3L, bd("50.00")), transfers.get(0));
    }

    @Test
    void alreadySettledReturnsNothing() {
        Map<Long, BigDecimal> balances = Map.of(1L, bd("0.00"), 2L, bd("0.00"));

        assertTrue(optimizer.optimize(balances).isEmpty());
    }

    @Test
    void handlesPaiseAmounts() {
        Map<Long, BigDecimal> balances = Map.of(
                1L, bd("1266.66"), 2L, bd("-633.33"), 3L, bd("-633.33"));

        List<Transfer> transfers = optimizer.optimize(balances);

        assertEquals(2, transfers.size());
        assertSettlesEveryone(balances, transfers);
    }

    @Test
    void twoCreditorsTwoDebtors_usesAtMostNMinusOnePayments() {
        Map<Long, BigDecimal> balances = Map.of(
                1L, bd("70.00"), 2L, bd("30.00"),
                3L, bd("-60.00"), 4L, bd("-40.00"));

        List<Transfer> transfers = optimizer.optimize(balances);

        assertTrue(transfers.size() <= 3);
        assertSettlesEveryone(balances, transfers);
    }

    @Test
    void sameInputGivesSameOutput() {
        Map<Long, BigDecimal> balances = Map.of(
                1L, bd("50.00"), 2L, bd("50.00"),
                3L, bd("-50.00"), 4L, bd("-50.00"));

        assertEquals(optimizer.optimize(balances), optimizer.optimize(balances));
    }

    @Test
    void rejectsBalancesThatDoNotAddUpToZero() {
        Map<Long, BigDecimal> balances = Map.of(1L, bd("100.00"), 2L, bd("-90.00"));

        assertThrows(IllegalArgumentException.class, () -> optimizer.optimize(balances));
    }
}