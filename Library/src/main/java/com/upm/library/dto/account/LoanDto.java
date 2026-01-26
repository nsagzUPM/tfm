package com.upm.library.dto.account;

import com.upm.library.domain.Loan;

public record LoanDto(Long id, Long copyId, String dueDate, boolean closed, int renewals) {
    public static LoanDto from(Loan l) {
        return new LoanDto(
                l.getId(),
                l.getCopy().getId(),
                l.getDueDate().toString(),
                l.isClosed(),
                l.getRenewals()
        );
    }
}