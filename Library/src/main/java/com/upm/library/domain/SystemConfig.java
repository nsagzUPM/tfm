package com.upm.library.domain;


import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import org.springframework.data.annotation.Version;

@Entity
@Table(name = "system_config")
public class SystemConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // siempre 1

    @Min(1)
    @Column(name = "student_loan_days", nullable = false)
    private int studentLoanDays;

    @Min(1)
    @Column(name = "professor_loan_days", nullable = false)
    private int professorLoanDays;

    @Min(0)
    @Column(name = "penalty_days_per_late_day", nullable = false)
    private int penaltyDaysPerLateDay;

    @Min(1)
    @Column(name = "reservation_days", nullable = false)
    private int reservationDays;

    @Version
    private long version; // evita pisados concurrentes (opcional pero recomendado)

    public SystemConfig(Long id,
                        int studentLoanDays,
                        int professorLoanDays,
                        int penaltyDaysPerLateDay,
                        int reservationDays) {
        this.id = id;
        this.studentLoanDays = studentLoanDays;
        this.professorLoanDays = professorLoanDays;
        this.penaltyDaysPerLateDay = penaltyDaysPerLateDay;
        this.reservationDays = reservationDays;
    }

    public SystemConfig() {

    }

    public void update(int studentLoanDays,
                       int professorLoanDays,
                       int penaltyDaysPerLateDay,
                       int reservationDays) {

        this.studentLoanDays = studentLoanDays;
        this.professorLoanDays = professorLoanDays;
        this.penaltyDaysPerLateDay = penaltyDaysPerLateDay;
        this.reservationDays = reservationDays;
    }

    public int getPenaltyDaysPerLateDay() {
        return penaltyDaysPerLateDay;
    }

    public void setPenaltyDaysPerLateDay(int penaltyDaysPerLateDay) {
        this.penaltyDaysPerLateDay = penaltyDaysPerLateDay;
    }

    public int getReservationDays() {
        return reservationDays;
    }

    public void setReservationDays(int reservationDays) {
        this.reservationDays = reservationDays;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public int getProfessorLoanDays() {
        return professorLoanDays;
    }

    public void setProfessorLoanDays(int professorLoanDays) {
        this.professorLoanDays = professorLoanDays;
    }

    public int getStudentLoanDays() {
        return studentLoanDays;
    }

    public void setStudentLoanDays(int studentLoanDays) {
        this.studentLoanDays = studentLoanDays;
    }
}
