package com.upm.library.dto.account;


import com.upm.library.domain.Loan;
import com.upm.library.domain.Penalty;
import com.upm.library.domain.Reservation;

import java.util.List;

public record MyAccountDto(
        List<LoanDto> loans,
        List<ReservationSummaryDto> reservations,
        List<PenaltyDto> penalties
) {
    @SuppressWarnings("unchecked")
    public static MyAccountDto from(Object[] data) {
        List<Loan> loans = (List<Loan>) data[0];
        List<Reservation> reservations = (List<Reservation>) data[1];
        List<Penalty> penalties = (List<Penalty>) data[2];

        return new MyAccountDto(
                loans.stream().map(LoanDto::from).toList(),
                reservations.stream().map(ReservationSummaryDto::from).toList(),
                penalties.stream().map(PenaltyDto::from).toList()
        );
    }
}

