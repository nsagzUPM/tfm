package com.upm.library.service;

import com.upm.library.domain.*;
import com.upm.library.exception.BusinessRuleException;
import com.upm.library.exception.NotFoundException;
import com.upm.library.repository.*;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoanService {

    private final UserRepository userRepository;
    private final CopyRepository copyRepository;
    private final LoanRepository loanRepository;
    private final PenaltyRepository penaltyRepository;

    // MVP constants (later move to DB config)
    private static final int LOAN_DAYS = 14;
    private static final int LATE_RETURN_PENALTY_DAYS = 7;
    private static final int MAX_RENEWALS = 1;

    public LoanService(
            UserRepository userRepository,
            CopyRepository copyRepository,
            LoanRepository loanRepository,
            PenaltyRepository penaltyRepository
    ) {
        this.userRepository = userRepository;
        this.copyRepository = copyRepository;
        this.loanRepository = loanRepository;
        this.penaltyRepository = penaltyRepository;
    }

    @Transactional
    public Loan createLoan(Long userId, Long copyId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found."));

        boolean hasActivePenalty = penaltyRepository.existsByUserIdAndActiveIsTrueAndEndDateGreaterThanEqual(
                user.getId(), LocalDate.now()
        );
        if (hasActivePenalty) {
            throw new BusinessRuleException("User has an active penalty.");
        }

        Copy copy = copyRepository.findById(copyId)
                .orElseThrow(() -> new NotFoundException("Copy not found."));

        if (copy.isReferenceOnly()) {
            throw new BusinessRuleException("Reference-only copies cannot be loaned.");
        }
        if (copy.getStatus() == CopyStatus.LOANED) {
            throw new BusinessRuleException("Copy is already loaned.");
        }
        // If RESERVED, MVP accepts it (real rule may require "reserved by same user")
        if (copy.getStatus() != CopyStatus.AVAILABLE && copy.getStatus() != CopyStatus.RESERVED) {
            throw new BusinessRuleException("Copy cannot be loaned in its current status.");
        }

        LocalDate today = LocalDate.now();
        Loan loan = new Loan(user, copy, today, today.plusDays(LOAN_DAYS));
        copy.markAsLoaned();

        copyRepository.save(copy);
        return loanRepository.save(loan);
    }

    @Transactional
    public void registerReturn(Long copyId) {
        Copy copy = copyRepository.findById(copyId)
                .orElseThrow(() -> new NotFoundException("Copy not found."));

        Loan openLoan = loanRepository.findAll().stream()
                .filter(l -> !l.isClosed() && l.getCopy().getId().equals(copyId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("No open loan found for this copy."));

        openLoan.close();
        copy.markAsAvailable();

        LocalDate today = LocalDate.now();
        if (today.isAfter(openLoan.getDueDate())) {
            Penalty penalty = new Penalty(
                    openLoan.getUser(),
                    today,
                    today.plusDays(LATE_RETURN_PENALTY_DAYS),
                    "Late return"
            );
            penaltyRepository.save(penalty);
        }

        loanRepository.save(openLoan);
        copyRepository.save(copy);
    }

    @Transactional
    public Loan renewLoan(String externalUserId, Long loanId) {
        User user = userRepository.findByExternalId(externalUserId)
                .orElseThrow(() -> new NotFoundException("User not found."));

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new NotFoundException("Loan not found."));

        if (!loan.getUser().getId().equals(user.getId())) {
            throw new BusinessRuleException("You cannot renew another user's loan.");
        }
        if (loan.isClosed()) {
            throw new BusinessRuleException("Closed loans cannot be renewed.");
        }
        if (loan.getRenewals() >= MAX_RENEWALS) {
            throw new BusinessRuleException("Maximum renewals reached.");
        }

        loan.renew(loan.getDueDate().plusDays(LOAN_DAYS));
        return loanRepository.save(loan);
    }

    public List<User> searchUsers(String query) {
        if (query == null || query.isBlank()) {
            return userRepository.findAll();
        }

        String q = "%" + query.trim().toLowerCase() + "%";

        Specification<User> spec = (root, cq, cb) ->
                cb.or(
                        //cb.like(cb.lower(root.get("id")), q),
                        cb.like(cb.lower(root.get("externalId")), q),
                        cb.like(cb.lower(root.get("name")), q)
                );

        return userRepository.findAll(spec);
    }
}
