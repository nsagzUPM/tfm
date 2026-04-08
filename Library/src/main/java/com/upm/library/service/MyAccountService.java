package com.upm.library.service;

import com.upm.library.domain.ReservationStatus;
import com.upm.library.domain.User;
import com.upm.library.exception.NotFoundException;
import com.upm.library.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MyAccountService {

    private final UserRepository userRepository;
    private final LoanRepository loanRepository;
    private final ReservationRepository reservationRepository;
    private final PenaltyRepository penaltyRepository;

    public MyAccountService(
            UserRepository userRepository,
            LoanRepository loanRepository,
            ReservationRepository reservationRepository,
            PenaltyRepository penaltyRepository
    ) {
        this.userRepository = userRepository;
        this.loanRepository = loanRepository;
        this.reservationRepository = reservationRepository;
        this.penaltyRepository = penaltyRepository;
    }

    public Object[] getActiveAccountData(String externalUserId) {
        User user = userRepository.findByExternalId(externalUserId)
                .orElseThrow(() -> new NotFoundException("User not found."));

        return new Object[] {
                loanRepository.findByUserIdAndClosed(user.getId(), false),
                reservationRepository.findByUserIdAndStatusInOrderByCreatedAtAsc(user.getId(), List.of(
                        ReservationStatus.ACTIVE,
                        ReservationStatus.QUEUED
                )),
                penaltyRepository.findByUserIdAndActiveIsTrue(user.getId())
        };
    }

}
