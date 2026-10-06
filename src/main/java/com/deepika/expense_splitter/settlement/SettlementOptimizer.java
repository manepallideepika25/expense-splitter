package com.deepika.expense_splitter.settlement;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

@Component
public class SettlementOptimizer {
    public List<Transfer> optimize(Map<Long, BigDecimal> netBalances)
    {
        BigDecimal total=netBalances.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if(total.signum()!=0)
        {
            throw new IllegalArgumentException("balances must add up to zero but add up to "+ total);
        }
        PriorityQueue<Balance> creditors=new PriorityQueue<>(Balance::compareLargestFirst);
        PriorityQueue<Balance>debtors= new PriorityQueue<>(Balance::compareLargestFirst);
        for (Map.Entry<Long, BigDecimal> entry : netBalances.entrySet()) {
            BigDecimal amount = entry.getValue();
            if (amount.signum() > 0) {
                creditors.add(new Balance(entry.getKey(), amount));
            } else if (amount.signum() < 0) {
                debtors.add(new Balance(entry.getKey(), amount.negate()));
            }
        }
        List<Transfer> transfers = new ArrayList<>();
        while (!debtors.isEmpty() && !creditors.isEmpty()) {
            Balance debtor = debtors.poll();
            Balance creditor = creditors.poll();

            BigDecimal amount = debtor.amount.min(creditor.amount);
            transfers.add(new Transfer(debtor.userId, creditor.userId, amount));

            debtor.amount = debtor.amount.subtract(amount);
            creditor.amount = creditor.amount.subtract(amount);

            if (debtor.amount.signum() > 0) {
                debtors.add(debtor);
            }
            if (creditor.amount.signum() > 0) {
                creditors.add(creditor);
            }
        }
        return transfers;
    }
    private static class Balance {
        final Long userId;
        BigDecimal amount;

        Balance(Long userId, BigDecimal amount) {
            this.userId = userId;
            this.amount = amount;
        }

        int compareLargestFirst(Balance other) {
            int byAmount = other.amount.compareTo(this.amount);
            return byAmount != 0 ? byAmount : this.userId.compareTo(other.userId);
        }
    }

}
