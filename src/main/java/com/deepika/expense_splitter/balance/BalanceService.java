package com.deepika.expense_splitter.balance;

import com.deepika.expense_splitter.exception.ResourceNotFoundException;
import com.deepika.expense_splitter.expense.ExpenseRepository;
import com.deepika.expense_splitter.expense.ExpenseSplitRepository;
import com.deepika.expense_splitter.expense.UserAmount;
import com.deepika.expense_splitter.group.GroupMember;
import com.deepika.expense_splitter.group.GroupMemberRepository;
import com.deepika.expense_splitter.group.GroupRepository;
import com.deepika.expense_splitter.settlement.SettlementRepository;
import com.deepika.expense_splitter.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

@Service
public class BalanceService {
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final ExpenseSplitRepository expenseSplitRepository;
    private final ExpenseRepository expenseRepository;
    private final SettlementRepository settlementRepository;

    public BalanceService(GroupRepository groupRepository, GroupMemberRepository groupMemberRepository,
                          ExpenseSplitRepository expenseSplitRepository, ExpenseRepository expenseRepository,
                          SettlementRepository settlementRepository) {
        this.groupRepository = groupRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.expenseSplitRepository = expenseSplitRepository;
        this.expenseRepository = expenseRepository;
        this.settlementRepository = settlementRepository;
    }

    @Transactional(readOnly = true)
    public List<MemberBalance> getBalances(Long groupId) {
        if (!groupRepository.existsById(groupId)) {
            throw new ResourceNotFoundException("Group not found with id " + groupId);
        }

        Map<Long, BigDecimal> paid = toMap(expenseRepository.sumPaidByUser(groupId));
        Map<Long, BigDecimal> shares = toMap(expenseSplitRepository.sumSharesByUser(groupId));
        Map<Long, BigDecimal> made = toMap(settlementRepository.sumMadeByUser(groupId));
        Map<Long, BigDecimal> received = toMap(settlementRepository.sumReceivedByUser(groupId));

        return groupMemberRepository.findByGroupId(groupId).stream()
                .map(GroupMember::getUser)
                .map(user -> toBalance(user, paid, shares, made, received))
                .toList();
    }
    private MemberBalance toBalance(User user,
                                    Map<Long, BigDecimal> paid,
                                    Map<Long, BigDecimal> shares,
                                    Map<Long, BigDecimal> made,
                                    Map<Long, BigDecimal> received) {
        BigDecimal totalPaid = paid.getOrDefault(user.getId(), BigDecimal.ZERO);
        BigDecimal totalShare = shares.getOrDefault(user.getId(), BigDecimal.ZERO);
        BigDecimal settlementsMade = made.getOrDefault(user.getId(), BigDecimal.ZERO);
        BigDecimal settlementsReceived = received.getOrDefault(user.getId(), BigDecimal.ZERO);

        BigDecimal net = totalPaid.subtract(totalShare)
                .add(settlementsMade)
                .subtract(settlementsReceived);

        return new MemberBalance(user.getId(), user.getName(),
                totalPaid, totalShare, settlementsMade, settlementsReceived, net);
    }
    private Map<Long, BigDecimal> toMap(List<UserAmount> amounts) {
        return amounts.stream()
                .collect(Collectors.toMap(UserAmount::userId, UserAmount::total));
    }

}
