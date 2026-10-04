package com.deepika.expense_splitter.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequest (
        @NotBlank(message = "Name is required")
        String name,
        @NotBlank(message = "email is required")
        @Email(message = "Email must be valid")
        String email,
        @NotBlank(message = "password is required")
        @Size(min=6, message = "password must be at least 6 characters")
        String password
) {}
