package com.upm.library.dto.admin;

import com.upm.library.domain.Loan;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public record LoanAdminDto(Long id, LocalDate createdAt, LocalDate dueAt, Long userId, String userName,
                           String userEmail, Long copyId, String dueDate, String bookTitle, String bookAuthor,
                           long overdueDays) {
    public static LoanAdminDto from(Loan l) {
        return new LoanAdminDto(
                l.getId(),
                l.getStartDate(),
                l.getDueDate(),
                l.getUser().getId(),
                l.getUser().getName(),
                l.getUser().getExternalId(),
                l.getCopy().getId(),
                l.getDueDate().toString(),
                l.getCopy().getBook().getTitle(),
                l.getCopy().getBook().getAuthor(),
                ChronoUnit.DAYS.between(l.getDueDate(), LocalDate.now())
        );
    }
}