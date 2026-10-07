package com.deepika.expense_splitter.settlement;

import com.deepika.expense_splitter.expense.UserAmount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SettlementRepository extends JpaRepository<Settlement,Long> {
    List<Settlement> findByGroupIdOrderBySettledAtDescIdDesc(Long groupId);
    @Query("""
            select new com.deepika.expense_splitter.expense.UserAmount(s.fromUser.id, sum(s.amount))
            from Settlement s
            where s.group.id = :groupId
            group by s.fromUser.id
            """)
    List<UserAmount> sumMadeByUser(@Param("groupId") Long groupId);

    @Query("""
            select new com.deepika.expense_splitter.expense.UserAmount(s.toUser.id, sum(s.amount))
            from Settlement s
            where s.group.id = :groupId
            group by s.toUser.id
            """)
    List<UserAmount> sumReceivedByUser(@Param("groupId") Long groupId);
}
