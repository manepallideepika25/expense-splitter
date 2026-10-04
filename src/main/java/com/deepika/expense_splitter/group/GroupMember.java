package com.deepika.expense_splitter.group;

import com.deepika.expense_splitter.user.User;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "group_members",
uniqueConstraints = @UniqueConstraint(columnNames = {"group_id","user_id"}))
public class GroupMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(nullable = false, updatable = false)
    private LocalDateTime joinedAt;
    @PrePersist
    void onCreate() {
        this.joinedAt = LocalDateTime.now();
    }

    public GroupMember() {
    }

    public GroupMember(Group group, User user) {
        this.group = group;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public Group getGroup() {
        return group;
    }

    public User getUser() {
        return user;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }
}
