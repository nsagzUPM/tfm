package com.upm.library.service;

import com.upm.library.domain.*;
import com.upm.library.exception.BusinessRuleException;
import com.upm.library.exception.NotFoundException;
import com.upm.library.repository.*;

import java.time.LocalDate;
import java.util.List;

import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReservationService {

    private final UserRepository userRepository;
    private final CopyRepository copyRepository;
    private final ReservationRepository reservationRepository;
    private final PenaltyRepository penaltyRepository;
    private final SystemConfigService systemConfigService;

    public ReservationService(
            UserRepository userRepository,
            CopyRepository copyRepository,
            ReservationRepository reservationRepository,
            PenaltyRepository penaltyRepository, SystemConfigService systemConfigService
    ) {
        this.userRepository = userRepository;
        this.copyRepository = copyRepository;
        this.reservationRepository = reservationRepository;
        this.penaltyRepository = penaltyRepository;
        this.systemConfigService = systemConfigService;
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
        boolean copyAvailable =
                copy.getStatus() == CopyStatus.AVAILABLE;

        boolean hasActiveOrQueued =
                reservationRepository.existsByCopyAndStatusIn(
                        copy,
                        List.of(ReservationStatus.ACTIVE, ReservationStatus.QUEUED)
                );
        Reservation reservation = new Reservation(user, copy);
        reservation.setDeadline(LocalDate.now().plusDays(systemConfigService.getReservationDays()));
        if (copyAvailable && !hasActiveOrQueued) {
            reservation.setStatus(ReservationStatus.ACTIVE);
            copy.markAsReserved();
        } else {
            reservation.setStatus(ReservationStatus.QUEUED);
        }

        copyRepository.saveAndFlush(copy);
        return reservationRepository.saveAndFlush(reservation);
    }

    @Transactional
    public void promoteNextReservation(Copy copy) {
        reservationRepository
                .findFirstByCopyAndStatusOrderByCreatedAtAsc(
                        copy,
                        ReservationStatus.QUEUED
                )
                .ifPresent(reservation -> {
                    reservation.setStatus(ReservationStatus.ACTIVE);
                    reservation.setDeadline(LocalDate.now().plusDays(systemConfigService.getReservationDays()));
                    copy.markAsReserved();

                    reservationRepository.save(reservation);
                    copyRepository.save(copy);
                });
    }

    public boolean hasQueuedReservations(Copy copy) {
        return reservationRepository.existsByCopyAndStatus(
                copy,
                ReservationStatus.QUEUED
        );
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
        if (reservation.getStatus() != ReservationStatus.ACTIVE && reservation.getStatus() != ReservationStatus.QUEUED) {
            throw new BusinessRuleException("Only active reservations can be canceled.");
        }
        ReservationStatus status = reservation.getStatus();
        reservation.cancel();
        reservationRepository.save(reservation);
        Copy copy = reservation.getCopy();

        if (status != ReservationStatus.ACTIVE) {
            return;
        }
        if (hasQueuedReservations(copy)) {
            promoteNextReservation(copy);
        } else {
            copy.markAsAvailable();
        }
        copyRepository.save(reservation.getCopy());
    }
}
