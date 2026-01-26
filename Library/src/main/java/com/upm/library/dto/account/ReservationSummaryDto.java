package com.upm.library.dto.account;

import com.upm.library.domain.Reservation;

public record ReservationSummaryDto(Long id, Long copyId, String status) {
    public static ReservationSummaryDto from(Reservation r) {
        return new ReservationSummaryDto(r.getId(), r.getCopy().getId(), r.getStatus().name());
    }
}

