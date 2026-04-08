package com.upm.library.dto.account;

import com.upm.library.domain.Reservation;

public record ReservationSummaryDto(Long id, Long copyId, String title, String status, String deadline) {
    public static ReservationSummaryDto from(Reservation r) {
        return new ReservationSummaryDto(
                r.getId(),
                r.getCopy().getId(),
                r.getCopy().getBook().getTitle(),
                r.getStatus().name(),
                r.getDeadline().toString());
    }
}

