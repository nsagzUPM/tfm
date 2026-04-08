package com.upm.library.controller;

import com.upm.library.domain.Book;
import com.upm.library.domain.Copy;
import com.upm.library.domain.ReservationStatus;
import com.upm.library.domain.User;
import com.upm.library.dto.catalog.BookSummaryDto;
import com.upm.library.dto.catalog.CopyDetailsDto;
import com.upm.library.repository.*;
import com.upm.library.service.CatalogService;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/catalog")
class CatalogController {
    private final CatalogService catalogService;
    private final CopyRepository copyRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final LoanRepository loanRepository;
    private final ReservationRepository reservationRepository;

    public CatalogController(CatalogService catalogService, CopyRepository copyRepository, BookRepository bookRepository, UserRepository userRepository, LoanRepository loanRepository, ReservationRepository reservationRepository) {
        this.catalogService = catalogService;
        this.copyRepository = copyRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.loanRepository = loanRepository;
        this.reservationRepository = reservationRepository;
    }

    @GetMapping
    public String search(@RequestParam(required = false, name = "q") String q, Model model) {
        model.addAttribute("q", q);
        model.addAttribute("books", (q == null || q.isBlank()) ? List.of() : catalogService.search(q)
                .stream().map(BookSummaryDto::from).toList());
        return "catalog";
    }

    @GetMapping("/copies/{id}")
    public String getCopyDetails(@AuthenticationPrincipal OidcUser user, @PathVariable Long id, Model model) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("book", book);
        User u = userRepository.findByExternalId(user.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        var copies = copyRepository.findByBookId(id);
        model.addAttribute("copies",copies.stream().map(CopyDetailsDto::from).toList());
        var copyIds = copies.stream().map(Copy::getId).toList();
        var blockedCopyIds = new java.util.HashSet<Long>();

        blockedCopyIds.addAll(
                loanRepository.findActiveCopyIdsByUserIdAndCopyIdIn(u.getId(), copyIds)
        );
        blockedCopyIds.addAll(
                reservationRepository.findCopyIdsByUserIdAndCopyIdInAndStatusIn(
                        u.getId(),
                        copyIds,
                        java.util.List.of(ReservationStatus.ACTIVE, ReservationStatus.QUEUED)
                )
        );

        model.addAttribute("blockedCopyIds", blockedCopyIds);
        return "catalog-copies";
    }
}
