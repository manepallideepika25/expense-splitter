package com.deepika.expense_splitter.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UpdateUserRequest(
        @NotBlank(message = "name is required")
        String name,
        @NotBlank(message = "email is required")
        @Email(message = "email must be valid")
        String email

)
{}
