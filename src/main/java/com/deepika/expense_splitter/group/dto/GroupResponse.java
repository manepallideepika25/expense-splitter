package com.deepika.expense_splitter.group.dto;

import java.time.LocalDateTime;
import java.util.List;

public record GroupResponse(
        Long id,
        String name,
        String description,
        Long createdBy,
        LocalDateTime createdAt,
        List<MemberResponse> members
) {}
