package com.deepika.expense_splitter.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
       @NotBlank(message = "Email is required")
       @Email(message = "Email must be valid")
       String email,
       @NotBlank(message = "password is required")
       String password
) {
}
