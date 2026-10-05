package com.deepika.expense_splitter.group.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateGroupRequest(
        @NotBlank(message = "Group name is required")
        String name,

        String description,

        @NotNull(message = "createdByUserId is required")
        Long createdByUserId
) {
}
