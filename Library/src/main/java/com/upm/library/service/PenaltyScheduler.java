package com.upm.library.service;

import com.upm.library.domain.Penalty;
import com.upm.library.repository.PenaltyRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
class PenaltyScheduler {
    private final PenaltyRepository penaltyRepository;

    public PenaltyScheduler(PenaltyRepository penaltyRepository) {
        this.penaltyRepository = penaltyRepository;
    }

    @Transactional
    public void deactivateExpiredPenalties() {

        LocalDate today = LocalDate.now();

        List<Penalty> expired = penaltyRepository
                .findByActiveTrueAndEndDateBefore(today);

        expired.forEach(Penalty::deactivate);
        penaltyRepository.saveAll(expired);
    }

    @Scheduled(cron = "0 5 0 * * *")
    public void deactivateExpiredPenaltiesDaily() {
        deactivateExpiredPenalties();
    }

    // al arrancar
    @EventListener(ApplicationReadyEvent.class)
    public void deactivateExpiredPenaltiesOnStartup() {
        deactivateExpiredPenalties();
    }
}
