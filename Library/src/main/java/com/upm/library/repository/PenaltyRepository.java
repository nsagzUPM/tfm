package com.upm.library.repository;

import com.upm.library.domain.Penalty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PenaltyRepository extends JpaRepository<Penalty, Long> {
    List<Penalty> findByUserIdAndActiveIsTrue(Long userId);
    List<Penalty> findByActiveTrueAndEndDateBefore(LocalDate date);
    boolean existsByUserIdAndActiveIsTrueAndEndDateGreaterThanEqual(Long userId, LocalDate today);
}
