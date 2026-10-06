package com.deepika.expense_splitter.settlement;

import com.deepika.expense_splitter.balance.BalanceService;
import com.deepika.expense_splitter.balance.MemberBalance;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class SettlementService {
    private final BalanceService balanceService;
    private final SettlementOptimizer optimizer;

    public SettlementService(BalanceService balanceService, SettlementOptimizer optimizer) {
        this.balanceService = balanceService;
        this.optimizer = optimizer;
    }
    @Transactional(readOnly = true)
    public List<SuggestedSettlement> getSuggestedSettlements(Long groupId)
    {
        List<MemberBalance> balances=balanceService.getBalances(groupId);
        Map<Long, String> names = new LinkedHashMap<>();
        Map<Long, BigDecimal> nets = new LinkedHashMap<>();
        for (MemberBalance b : balances) {
            names.put(b.userId(), b.name());
            nets.put(b.userId(), b.netBalance());
        }

        return optimizer.optimize(nets).stream()
                .map(t -> new SuggestedSettlement(
                        t.fromUserId(), names.get(t.fromUserId()),
                        t.toUserId(), names.get(t.toUserId()),
                        t.amount()))
                .toList();
    }

}
