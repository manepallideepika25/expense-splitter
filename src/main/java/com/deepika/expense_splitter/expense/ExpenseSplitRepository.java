package com.deepika.expense_splitter.expense;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface ExpenseSplitRepository extends JpaRepository<ExpenseSplit, Long> {

    List<ExpenseSplit> findByExpenseIdIn(Collection<Long> expenseIds);
    @Query("""
        select new com.deepika.expense_splitter.expense.UserAmount(s.user.id, sum(s.shareAmount))
        from ExpenseSplit s
        where s.expense.group.id = :groupId
        group by s.user.id
        """)
    List<UserAmount> sumSharesByUser(@Param("groupId") Long groupId);
}