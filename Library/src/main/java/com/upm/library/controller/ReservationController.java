package com.upm.library.controller;

import com.upm.library.domain.*;
import com.upm.library.dto.reservations.CreateReservationRequest;
import com.upm.library.dto.reservations.ReservationDto;
import com.upm.library.exception.BusinessRuleException;
import com.upm.library.repository.BookRepository;
import com.upm.library.repository.CopyRepository;
import com.upm.library.repository.UserRepository;
import com.upm.library.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final CopyRepository copyRepository;

    public ReservationController(ReservationService reservationService, CopyRepository copyRepository) {
        this.reservationService = reservationService;
        this.copyRepository = copyRepository;
    }

    @GetMapping("/{id}")
    public String create(@PathVariable Long id, Model model) {
        Copy copy = copyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("bookTitle", copy.getBook().getTitle());
        model.addAttribute("bookId", copy.getBook().getId());
        model.addAttribute("copyId", copy.getId());
        model.addAttribute("status", copy.getStatus());
        return "reservations";
    }

    @PostMapping("/confirm")
    public String create(@AuthenticationPrincipal OidcUser oidcUser, @RequestParam Long copyId, RedirectAttributes ra) {
        try {
            Reservation res = reservationService.createReservation(oidcUser.getEmail(), copyId);
            if (res.getStatus() == ReservationStatus.QUEUED) {
                ra.addFlashAttribute(
                        "success",
                        "Ejemplar reservado. Has sido añadido a la cola ⏳"
                );
            } else {
                ra.addFlashAttribute(
                        "success",
                        "Reserva creada con éxito ✅"
                );
            }
            return "redirect:/my-account";
        } catch (BusinessRuleException ex) {
            ra.addFlashAttribute("reserveErr", ex.getMessage());
            return "redirect:/reservations/" + copyId;
        }
    }

    @DeleteMapping("/{id}")
    public String cancel(@AuthenticationPrincipal OidcUser oidcUser, @PathVariable Long id, RedirectAttributes ra) {
        try {
            reservationService.cancelReservation(oidcUser.getEmail(), id);
            ra.addFlashAttribute("reserveOk", "Reserva eliminada");
        } catch (BusinessRuleException ex) {
            ra.addFlashAttribute("reserveErr", ex.getMessage());
        }
        return "redirect:/my-account";
    }
}
