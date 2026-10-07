package com.deepika.expense_splitter.settlement;

import com.deepika.expense_splitter.balance.BalanceService;
import com.deepika.expense_splitter.balance.MemberBalance;
import com.deepika.expense_splitter.exception.BadRequestException;
import com.deepika.expense_splitter.exception.ResourceNotFoundException;
import com.deepika.expense_splitter.group.Group;
import com.deepika.expense_splitter.group.GroupMember;
import com.deepika.expense_splitter.group.GroupMemberRepository;
import com.deepika.expense_splitter.group.GroupRepository;
import com.deepika.expense_splitter.settlement.dto.CreateSettlementRequest;
import com.deepika.expense_splitter.settlement.dto.SettlementResponse;
import com.deepika.expense_splitter.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SettlementService {
    private final BalanceService balanceService;
    private final SettlementOptimizer optimizer;
    private final SettlementRepository settlementRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;

    public SettlementService(BalanceService balanceService, SettlementOptimizer optimizer,
                             SettlementRepository settlementRepository, GroupRepository groupRepository,
                             GroupMemberRepository groupMemberRepository) {
        this.balanceService = balanceService;
        this.optimizer = optimizer;
        this.settlementRepository = settlementRepository;
        this.groupRepository = groupRepository;
        this.groupMemberRepository = groupMemberRepository;
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
    @Transactional
    public SettlementResponse recordSettlement(Long groupId, CreateSettlementRequest request) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with id " + groupId));

        if (request.fromUserId().equals(request.toUserId())) {
            throw new BadRequestException("Payer and receiver must be different people");
        }
        if (request.amount().stripTrailingZeros().scale() > 2) {
            throw new BadRequestException("Amount can have at most 2 decimal places");
        }

        Map<Long, User> members = groupMemberRepository.findByGroupId(groupId).stream()
                .map(GroupMember::getUser)
                .collect(Collectors.toMap(User::getId, Function.identity()));

        User from = members.get(request.fromUserId());
        User to = members.get(request.toUserId());
        if (from == null || to == null) {
            throw new BadRequestException("Both users must be members of this group");
        }

        Settlement saved = settlementRepository.save(new Settlement(
                group, from, to, request.amount().setScale(2, RoundingMode.UNNECESSARY)));
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<SettlementResponse> getSettlements(Long groupId) {
        if (!groupRepository.existsById(groupId)) {
            throw new ResourceNotFoundException("Group not found with id " + groupId);
        }
        return settlementRepository.findByGroupIdOrderBySettledAtDescIdDesc(groupId).stream()
                .map(this::toResponse)
                .toList();
    }

    private SettlementResponse toResponse(Settlement s) {
        return new SettlementResponse(
                s.getId(), s.getGroup().getId(),
                s.getFromUser().getId(), s.getFromUser().getName(),
                s.getToUser().getId(), s.getToUser().getName(),
                s.getAmount(), s.getSettledAt());
    }

}
