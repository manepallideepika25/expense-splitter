package com.deepika.expense_splitter.expense;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense,Long> {
    List<Expense> findByGroupIdOrderByExpenseDateDescIdDesc(Long groupId);
    @Query("""
        select new com.deepika.expense_splitter.expense.UserAmount(e.paidBy.id, sum(e.amount))
        from Expense e
        where e.group.id = :groupId
        group by e.paidBy.id
        """)
    List<UserAmount> sumPaidByUser(@Param("groupId") Long groupId);
}
