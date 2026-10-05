package com.deepika.expense_splitter.expense;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ExpenseSplitRepository extends JpaRepository<ExpenseSplit, Long> {

    List<ExpenseSplit> findByExpenseIdIn(Collection<Long> expenseIds);
}