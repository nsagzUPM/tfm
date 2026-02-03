package com.upm.library.service;

import com.upm.library.domain.ReservationStatus;
import com.upm.library.domain.User;
import com.upm.library.exception.NotFoundException;
import com.upm.library.repository.*;
import org.springframework.stereotype.Service;

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
                reservationRepository.findByUserIdAndStatus(user.getId(), ReservationStatus.ACTIVE),
                penaltyRepository.findByUserId(user.getId())
        };
    }

    public Object[] getAccountData(String externalUserId) {
        User user = userRepository.findByExternalId(externalUserId)
                .orElseThrow(() -> new NotFoundException("User not found."));

        return new Object[] {
                loanRepository.findByUserId(user.getId()),
                reservationRepository.findByUserId(user.getId()),
                penaltyRepository.findByUserId(user.getId())
        };
    }
}
