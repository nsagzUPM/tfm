package com.upm.library.dto.admin;

import com.upm.library.domain.Loan;
import jakarta.validation.constraints.NotNull;

public record LoanAdminDto(Long id, Long userId, Long copyId, String dueDate) {
    public static LoanAdminDto from(Loan l) {
        return new LoanAdminDto(
                l.getId(),
                l.getUser().getId(),
                l.getCopy().getId(),
                l.getDueDate().toString()
        );
    }
}