package com.upm.library.controller;

import com.upm.library.dto.reservations.CreateReservationRequest;
import com.upm.library.dto.reservations.ReservationDto;
import com.upm.library.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ReservationDto create(Authentication auth, @Valid @RequestBody CreateReservationRequest req) {
        String subject = ((Jwt) auth.getPrincipal()).getSubject();
        return ReservationDto.from(reservationService.createReservation(subject, req.copyId()));
    }

    @DeleteMapping("/{id}")
    public void cancel(Authentication auth, @PathVariable Long id) {
        String subject = ((Jwt) auth.getPrincipal()).getSubject();
        reservationService.cancelReservation(subject, id);
    }
}
