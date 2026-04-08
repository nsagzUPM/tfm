package com.upm.library.service;

import com.upm.library.domain.Role;
import com.upm.library.domain.SystemConfig;
import com.upm.library.dto.admin.SystemConfigForm;
import com.upm.library.repository.SystemConfigRepository;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

@Service
public class SystemConfigService {
    private static final long CONFIG_ID = 1L;

    private final SystemConfigRepository repo;

    public SystemConfigService(SystemConfigRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public SystemConfig getOrCreateDefault() {
        return repo.findById(CONFIG_ID).orElseGet(() ->
                repo.save(new SystemConfig(
                        CONFIG_ID,
                        15,
                        30,
                        2,
                        3
                ))
        );
    }


    public int getReservationDays() {
        return repo.findById(CONFIG_ID).get().getReservationDays();
    }

    public int getLoanDays(Role role) {
        if(role.equals(Role.TEACHER)){
            return repo.findById(CONFIG_ID).get().getProfessorLoanDays();
        }
        return repo.findById(CONFIG_ID).get().getStudentLoanDays();

    }

    @Transactional
    public void update(SystemConfigForm form) {
        SystemConfig c = getOrCreateDefault();

        c.update(
                form.studentLoanDays(),
                form.professorLoanDays(),
                form.penaltyDaysPerLateDay(),
                form.reservationDays()
        );
    }

    public SystemConfigForm toForm(SystemConfig c) {
        return new SystemConfigForm(
                c.getStudentLoanDays(),
                c.getProfessorLoanDays(),
                c.getPenaltyDaysPerLateDay(),
                c.getReservationDays()
        );
    }

    public long getPenaltyDays() {
        return repo.findById(CONFIG_ID).get().getPenaltyDaysPerLateDay();
    }
}
