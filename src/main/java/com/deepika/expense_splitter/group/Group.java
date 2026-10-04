package com.deepika.expense_splitter.group;

import com.deepika.expense_splitter.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "expense_groups")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    private String description;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Group() {
    }

    public Group(String name, String description, User createdBy) {
        this.name = name;
        this.description = description;
        this.createdBy = createdBy;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
