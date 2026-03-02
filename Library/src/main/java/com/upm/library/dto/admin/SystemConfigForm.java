package com.upm.library.dto.admin;

import jakarta.validation.constraints.Min;

public record SystemConfigForm(
        @Min(1) int studentLoanDays,
        @Min(1) int professorLoanDays,
        @Min(0) int penaltyDaysPerLateDay,
        @Min(1) int reservationDays
) {
}
