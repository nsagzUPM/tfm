package com.upm.library.service;

import com.upm.library.domain.*;
import com.upm.library.exception.BusinessRuleException;
import com.upm.library.exception.NotFoundException;
import com.upm.library.repository.*;
import java.time.LocalDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReservationService {

    private final UserRepository userRepository;
    private final CopyRepository copyRepository;
    private final ReservationRepository reservationRepository;
    private final PenaltyRepository penaltyRepository;

    public ReservationService(
            UserRepository userRepository,
            CopyRepository copyRepository,
            ReservationRepository reservationRepository,
            PenaltyRepository penaltyRepository
    ) {
        this.userRepository = userRepository;
        this.copyRepository = copyRepository;
        this.reservationRepository = reservationRepository;
        this.penaltyRepository = penaltyRepository;
    }

    @Transactional
    public Reservation createReservation(String externalUserId, Long copyId) {
        User user = userRepository.findByExternalId(externalUserId)
                .orElseThrow(() -> new NotFoundException("User not found."));

        boolean hasActivePenalty = penaltyRepository.existsByUserIdAndActiveIsTrueAndEndDateGreaterThanEqual(
                user.getId(), LocalDate.now()
        );
        if (hasActivePenalty) {
            throw new BusinessRuleException("You cannot reserve items while you have an active penalty.");
        }

        Copy copy = copyRepository.findById(copyId)
                .orElseThrow(() -> new NotFoundException("Copy not found."));

        if (copy.isReferenceOnly()) {
            throw new BusinessRuleException("Reference-only copies cannot be reserved.");
        }
        if (copy.getStatus() != CopyStatus.AVAILABLE) {
            throw new BusinessRuleException("Copy is not available.");
        }

        Reservation reservation = new Reservation(user, copy);
        copy.markAsReserved();

        copyRepository.save(copy);
        return reservationRepository.save(reservation);
    }

    @Transactional
    public void cancelReservation(String externalUserId, Long reservationId) {
        User user = userRepository.findByExternalId(externalUserId)
                .orElseThrow(() -> new NotFoundException("User not found."));

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NotFoundException("Reservation not found."));

        if (!reservation.getUser().getId().equals(user.getId())) {
            throw new BusinessRuleException("You cannot cancel another user's reservation.");
        }
        if (reservation.getStatus() != ReservationStatus.ACTIVE) {
            throw new BusinessRuleException("Only active reservations can be canceled.");
        }

        reservation.cancel();
        reservation.getCopy().markAsAvailable();

        reservationRepository.save(reservation);
        copyRepository.save(reservation.getCopy());
    }
}
