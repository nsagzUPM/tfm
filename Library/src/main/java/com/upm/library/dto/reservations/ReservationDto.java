package com.upm.library.dto.reservations;

import com.upm.library.domain.Reservation;

import java.time.LocalDate;

public record ReservationDto(Long id, String bookTitle, Long copyId, String status, LocalDate deadline) {
    public static ReservationDto from(Reservation r) {
        return new ReservationDto(r.getId(), r.getCopy().getBook().getTitle(), r.getCopy().getId(), r.getStatus().name(), r.getDeadline());
    }
}

