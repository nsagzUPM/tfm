package com.upm.library.dto.reservations;

import com.upm.library.domain.Reservation;

public record ReservationDto(Long id, Long copyId, String status) {
    public static ReservationDto from(Reservation r) {
        return new ReservationDto(r.getId(), r.getCopy().getId(), r.getStatus().name());
    }
}

