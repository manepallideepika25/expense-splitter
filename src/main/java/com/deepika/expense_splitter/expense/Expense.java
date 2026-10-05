package com.deepika.expense_splitter.expense;

import com.deepika.expense_splitter.group.Group;
import com.deepika.expense_splitter.user.User;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "expenses")
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;
    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "paid_by",nullable = false)
    private User paidBy;

    @Column(nullable = false)
    private String description;
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SplitType splitType;

    @Column(nullable = false)
    private LocalDate expenseDate;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Expense() {
    }

    public Expense(Group group, User paidBy, String description, BigDecimal amount,
                   SplitType splitType, LocalDate expenseDate) {
        this.group = group;
        this.paidBy = paidBy;
        this.description = description;
        this.amount = amount;
        this.splitType = splitType;
        this.expenseDate = expenseDate;
    }

    public Long getId() {
        return id;
    }

    public Group getGroup() {
        return group;
    }

    public User getPaidBy() {
        return paidBy;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public SplitType getSplitType() {
        return splitType;
    }

    public LocalDate getExpenseDate() {
        return expenseDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
