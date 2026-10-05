package com.deepika.expense_splitter.expense;

import com.deepika.expense_splitter.exception.BadRequestException;
import com.deepika.expense_splitter.exception.ResourceNotFoundException;
import com.deepika.expense_splitter.expense.dto.CreateExpenseRequest;
import com.deepika.expense_splitter.expense.dto.ExpenseResponse;
import com.deepika.expense_splitter.expense.dto.ParticipantRequest;
import com.deepika.expense_splitter.expense.dto.SplitResponse;
import com.deepika.expense_splitter.group.Group;
import com.deepika.expense_splitter.group.GroupMember;
import com.deepika.expense_splitter.group.GroupMemberRepository;
import com.deepika.expense_splitter.group.GroupRepository;
import com.deepika.expense_splitter.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final ExpenseSplitRepository expenseSplitRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final ExpenseSplitCalculator calculator;

    public ExpenseService(ExpenseRepository expenseRepository,
                          ExpenseSplitRepository expenseSplitRepository,
                          GroupRepository groupRepository,
                          GroupMemberRepository groupMemberRepository,
                          ExpenseSplitCalculator calculator) {
        this.expenseRepository = expenseRepository;
        this.expenseSplitRepository = expenseSplitRepository;
        this.groupRepository = groupRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.calculator = calculator;
    }

    @Transactional
    public ExpenseResponse createExpense(Long groupId, CreateExpenseRequest request) {
        Group group = findGroup(groupId);

        Map<Long, User> members = groupMemberRepository.findByGroupId(groupId).stream()
                .map(GroupMember::getUser)
                .collect(Collectors.toMap(User::getId, Function.identity()));

        User payer = members.get(request.paidByUserId());
        if (payer == null) {
            throw new BadRequestException("Payer is not a member of this group");
        }
        for (ParticipantRequest p : request.participants()) {
            if (p.userId() != null && !members.containsKey(p.userId())) {
                throw new BadRequestException("User " + p.userId() + " is not a member of this group");
            }
        }

        List<SplitInput> inputs = request.participants().stream()
                .map(p -> new SplitInput(p.userId(), p.value()))
                .toList();
        Map<Long, BigDecimal> shares =
                calculator.calculate(request.amount(), request.splitType(), inputs);

        LocalDate date = request.expenseDate() != null ? request.expenseDate() : LocalDate.now();
        Expense expense = expenseRepository.save(new Expense(
                group, payer, request.description().trim(),
                request.amount().setScale(2, RoundingMode.UNNECESSARY),
                request.splitType(), date));

        List<ExpenseSplit> splits = shares.entrySet().stream()
                .map(e -> new ExpenseSplit(expense, members.get(e.getKey()), e.getValue()))
                .toList();
        expenseSplitRepository.saveAll(splits);

        return toResponse(expense, splits);
    }

    @Transactional(readOnly = true)
    public List<ExpenseResponse> getExpenses(Long groupId) {
        findGroup(groupId);
        List<Expense> expenses =
                expenseRepository.findByGroupIdOrderByExpenseDateDescIdDesc(groupId);
        if (expenses.isEmpty()) {
            return List.of();
        }

        List<Long> ids = expenses.stream().map(Expense::getId).toList();
        Map<Long, List<ExpenseSplit>> splitsByExpense =
                expenseSplitRepository.findByExpenseIdIn(ids).stream()
                        .collect(Collectors.groupingBy(s -> s.getExpense().getId()));

        return expenses.stream()
                .map(e -> toResponse(e, splitsByExpense.getOrDefault(e.getId(), List.of())))
                .toList();
    }

    private Group findGroup(Long id) {
        return groupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with id " + id));
    }

    private ExpenseResponse toResponse(Expense expense, List<ExpenseSplit> splits) {
        List<SplitResponse> splitResponses = splits.stream()
                .map(s -> new SplitResponse(
                        s.getUser().getId(), s.getUser().getName(), s.getShareAmount()))
                .toList();
        return new ExpenseResponse(
                expense.getId(), expense.getGroup().getId(), expense.getDescription(),
                expense.getAmount(), expense.getPaidBy().getId(), expense.getPaidBy().getName(),
                expense.getSplitType(), expense.getExpenseDate(), splitResponses);
    }
}
