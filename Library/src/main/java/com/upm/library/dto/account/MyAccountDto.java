package com.upm.library.dto.account;


import com.upm.library.domain.Loan;
import com.upm.library.domain.Penalty;
import com.upm.library.domain.Reservation;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public record MyAccountDto(
        List<LoanDto> loans,
        List<ReservationSummaryDto> reservations,
        List<PenaltyDto> penalties
) {
    @SuppressWarnings("unchecked")
    public static MyAccountDto from(Object[] data) {
        List<Loan> loans = Optional.ofNullable((List<Loan>) data[0])
                .orElse(Collections.emptyList());

        List<Reservation> reservations = Optional.ofNullable((List<Reservation>) data[1])
                .orElse(Collections.emptyList());

        List<Penalty> penalties = Optional.ofNullable((List<Penalty>) data[2])
                .orElse(Collections.emptyList());

        return new MyAccountDto(
                loans.stream().map(LoanDto::from).toList(),
                reservations.stream().map(ReservationSummaryDto::from).toList(),
                penalties.stream().map(PenaltyDto::from).toList()
        );
    }
}

