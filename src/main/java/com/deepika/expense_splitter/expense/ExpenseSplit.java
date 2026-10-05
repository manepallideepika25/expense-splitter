package com.deepika.expense_splitter.expense;

import com.deepika.expense_splitter.user.User;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "expense_splits",
        uniqueConstraints = @UniqueConstraint(columnNames = {"expense_id", "user_id"}))
public class ExpenseSplit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "expense_id", nullable = false)
    private Expense expense;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal shareAmount;

    public ExpenseSplit() {
    }

    public ExpenseSplit(Expense expense, User user, BigDecimal shareAmount) {
        this.expense = expense;
        this.user = user;
        this.shareAmount = shareAmount;
    }

    public Long getId() { return id; }
    public Expense getExpense() { return expense; }
    public User getUser() { return user; }
    public BigDecimal getShareAmount() { return shareAmount; }
}