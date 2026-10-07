package com.deepika.expense_splitter.auth;
import com.deepika.expense_splitter.user.dto.UserResponse;

public record AuthResponse(
        String accessToken,
        String tokenType,
        long expiresInSeconds,
        UserResponse user
) {
}
