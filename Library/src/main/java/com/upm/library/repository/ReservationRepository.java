package com.upm.library.repository;

import com.upm.library.domain.Copy;
import com.upm.library.domain.Reservation;
import com.upm.library.domain.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Optional<Reservation> findByUserIdAndCopyIdAndStatus(Long userId, Long copyId, ReservationStatus status);
    boolean existsByUserIdAndCopyIdAndStatus(Long userId, Long copyId, ReservationStatus status);
    boolean existsByCopyAndStatusIn(Copy copy,List<ReservationStatus> statuses);
    Optional<Reservation> findFirstByCopyAndStatusOrderByCreatedAtAsc(Copy copy,ReservationStatus status);
    List<Reservation> findByUserIdAndStatusInOrderByCreatedAtAsc(Long id, List<ReservationStatus> active);
    boolean existsByCopyAndStatus(Copy copy, ReservationStatus reservationStatus);
    @Query("""
       select r.copy.id
       from Reservation r
       where r.user.id = :userId
         and r.copy.id in :copyIds
         and r.status in :statuses
       """)
    List<Long> findCopyIdsByUserIdAndCopyIdInAndStatusIn(Long userId, List<Long> copyIds, List<ReservationStatus> statuses);
}
