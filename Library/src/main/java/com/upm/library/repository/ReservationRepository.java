package com.upm.library.repository;

import com.upm.library.domain.Reservation;
import com.upm.library.domain.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUserId(Long userId);
    List<Reservation> findByUserIdAndStatus(Long userId, ReservationStatus status);
}
