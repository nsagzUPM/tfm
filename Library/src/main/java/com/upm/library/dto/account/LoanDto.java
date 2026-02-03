package com.upm.library.dto.account;

import com.upm.library.domain.Loan;

public record LoanDto(Long id, Long copyId, String title,  boolean isReference, String dueDate,boolean closed, int renewals) {
    public static LoanDto from(Loan l) {
        return new LoanDto(
                l.getId(),
                l.getCopy().getId(),
                l.getCopy().getBook().getTitle(),
                l.getCopy().isReferenceOnly(),
                l.getDueDate().toString(),
                l.isClosed(),
                l.getRenewals()
        );
    }
}