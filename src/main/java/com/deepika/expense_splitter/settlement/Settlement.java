package com.deepika.expense_splitter.settlement;

import com.deepika.expense_splitter.group.Group;
import com.deepika.expense_splitter.user.User;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="settlements")
public class Settlement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "group_id",nullable = false)
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "from_user_id", nullable = false)
    private User fromUser;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "to_user_id",nullable = false)
    private User toUser;

    @Column(nullable = false, precision = 12,scale = 2)
    private BigDecimal amount;

    @Column(nullable = false,updatable = false)
    private LocalDateTime settledAt;
    @PrePersist
    void onCreate()
    {
        this.settledAt=LocalDateTime.now();
    }

    public Settlement() {
    }

    public Settlement(Group group, User fromUser, User toUser, BigDecimal amount) {
        this.group = group;
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.amount = amount;
    }

    public Long getId() {
        return id;
    }

    public Group getGroup() {
        return group;
    }

    public User getFromUser() {
        return fromUser;
    }

    public User getToUser() {
        return toUser;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDateTime getSettledAt() {
        return settledAt;
    }
}
