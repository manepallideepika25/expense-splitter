package com.deepika.expense_splitter.expense;

import com.deepika.expense_splitter.exception.BadRequestException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Component
public class ExpenseSplitCalculator {
    private static final BigDecimal HUNDRED= new BigDecimal("100");

    public Map<Long, BigDecimal> calculate(BigDecimal total, SplitType type,
                                           List<SplitInput> participants)
    {
        validateCommon(total, participants);
        return switch (type)
        {
            case EQUAL -> equalSplit(total,participants);
            case EXACT -> exactSplit(total,participants);
            case PERCENTAGE -> percentageSplit(total,participants);
        };
    }
    private Map<Long, BigDecimal> equalSplit(BigDecimal total, List<SplitInput> participants)
    {
        long totalPaise = total.movePointRight(2).longValueExact();
        int n = participants.size();
        long base = totalPaise / n;
        long remainder = totalPaise % n;

        Map<Long, BigDecimal> result = new LinkedHashMap<>();
        for (int i = 0; i < n; i++) {
            long paise = base + (i < remainder ? 1 : 0);
            result.put(participants.get(i).userId(), BigDecimal.valueOf(paise, 2));
        }
        return result;
    }
    private Map<Long, BigDecimal> exactSplit(BigDecimal total, List<SplitInput> participants) {
        Map<Long, BigDecimal> result = new LinkedHashMap<>();
        BigDecimal sum = BigDecimal.ZERO;

        for (SplitInput p : participants) {
            if (p.value() == null || p.value().signum() <= 0) {
                throw new BadRequestException("Each exact amount must be greater than zero");
            }
            BigDecimal amount = toMoney(p.value());
            result.put(p.userId(), amount);
            sum = sum.add(amount);
        }
        if (sum.compareTo(total) != 0) {
            throw new BadRequestException(
                    "Exact amounts add up to " + sum + " but the total is " + total);
        }
        return result;
    }
    private Map<Long, BigDecimal> percentageSplit(BigDecimal total, List<SplitInput> participants) {
        BigDecimal percentSum = BigDecimal.ZERO;
        for (SplitInput p : participants) {
            if (p.value() == null || p.value().signum() <= 0) {
                throw new BadRequestException("Each percentage must be greater than zero");
            }
            percentSum = percentSum.add(p.value());
        }
        if (percentSum.compareTo(HUNDRED) != 0) {
            throw new BadRequestException("Percentages add up to " + percentSum + " but must be 100");
        }

        BigDecimal[] shares = new BigDecimal[participants.size()];
        BigDecimal shareSum = BigDecimal.ZERO;
        for (int i = 0; i < participants.size(); i++) {
            shares[i] = total.multiply(participants.get(i).value())
                    .divide(HUNDRED, 2, RoundingMode.DOWN);
            shareSum = shareSum.add(shares[i]);
        }

        long leftoverPaise = total.subtract(shareSum).movePointRight(2).longValueExact();
        Map<Long, BigDecimal> result = new LinkedHashMap<>();
        for (int i = 0; i < participants.size(); i++) {
            BigDecimal share = shares[i];
            if (i < leftoverPaise) {
                share = share.add(new BigDecimal("0.01"));
            }
            result.put(participants.get(i).userId(), share);
        }
        return result;
    }

    private void validateCommon(BigDecimal total, List<SplitInput> participants) {
        if (total == null || total.signum() <= 0) {
            throw new BadRequestException("Amount must be greater than zero");
        }
        if (total.stripTrailingZeros().scale() > 2) {
            throw new BadRequestException("Amount can have at most 2 decimal places");
        }
        if (participants == null || participants.isEmpty()) {
            throw new BadRequestException("At least one participant is required");
        }
        Set<Long> seen = new HashSet<>();
        for (SplitInput p : participants) {
            if (p.userId() == null || !seen.add(p.userId())) {
                throw new BadRequestException("Each participant must appear exactly once");
            }
        }
    }

    private BigDecimal toMoney(BigDecimal value) {
        try {
            return value.setScale(2, RoundingMode.UNNECESSARY);
        } catch (ArithmeticException e) {
            throw new BadRequestException("Amounts can have at most 2 decimal places");
        }
    }
}
