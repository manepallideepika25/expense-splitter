package com.deepika.expense_splitter.expense;

import com.deepika.expense_splitter.expense.dto.CreateExpenseRequest;
import com.deepika.expense_splitter.expense.dto.ExpenseResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups/{groupId}/expenses")
public class ExpenseController {
    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExpenseResponse createExpense(@PathVariable Long groupId,
                                         @Valid @RequestBody CreateExpenseRequest request) {
        return expenseService.createExpense(groupId, request);
    }

    @GetMapping
    public List<ExpenseResponse> getExpenses(@PathVariable Long groupId) {
        return expenseService.getExpenses(groupId);
    }
}
