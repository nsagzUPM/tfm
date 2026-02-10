package com.upm.library.controller.admin;

import com.upm.library.domain.Loan;
import com.upm.library.dto.admin.*;
import com.upm.library.dto.catalog.CopyDetailsDto;
import com.upm.library.exception.NotFoundException;
import com.upm.library.repository.CopyRepository;
import com.upm.library.repository.LoanRepository;
import com.upm.library.repository.ReservationRepository;
import com.upm.library.repository.UserRepository;
import com.upm.library.service.LoanService;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class LoanAdminController {

    private final LoanService loanService;
    private final LoanRepository loanRepository;

    public LoanAdminController(LoanService loanService, LoanRepository loanRepository) {
        this.loanService = loanService;
        this.loanRepository = loanRepository;
    }

    @GetMapping("/loan")
    public String createLoan(
            @RequestParam(required = false) String userQ,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String copyCode,
            Model model
    ) {
        try {
            AdminLoanView vm = loanService.buildAdminLoanView(userQ, userId, copyCode);

            model.addAttribute("userQ", vm.userQ());
            model.addAttribute("users", vm.users());
            model.addAttribute("selectedUser", vm.selectedUser());
            model.addAttribute("copy", vm.copy());
            model.addAttribute("reservedForUser", vm.reservedForSelectedUser());
            model.addAttribute("userHasPenalty", vm.userHasPenalty());

            return "admin-loan";
        } catch (BadRequestException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/loan/confirm")
    public String confirmLoan(
            @RequestParam Long userId,
            @RequestParam Long copyId,
            @RequestParam(required = false, name = "userQ") String userQ,
            RedirectAttributes ra
    ) {
        try {
            Loan loan = loanService.createLoan(userId, copyId);
            ra.addFlashAttribute("toastOk",
                    "Préstamo registrado (Usuario " + userId + ", Ejemplar " + copyId + ")");
            ra.addAttribute("userQ", userQ);
            ra.addAttribute("userId", userId);
            return "redirect:/admin/loan";

        } catch (Exception ex) {
            ra.addFlashAttribute("toastErr",
                    (ex.getMessage() != null && !ex.getMessage().isBlank())
                            ? ex.getMessage()
                            : "No se pudo registrar el préstamo.");
            ra.addAttribute("userQ", userQ);
            ra.addAttribute("userId", userId);
            ra.addAttribute("copyId", copyId);
            return "redirect:/admin/loan";
        }
    }

    @GetMapping("/return")
    public String registerReturn(@RequestParam(required = false) Long copyId, Model model) {
        if (copyId != null) {
            loanRepository.findByCopyIdAndClosed(copyId, false)
                    .map(LoanAdminDto::from)
                    .ifPresentOrElse(
                            loan -> model.addAttribute("loan", loan),
                            () -> model.addAttribute("toastErr", "No se encontró un préstamo activo para el ejemplar " + copyId)
                    );
        }
        return "admin-return";
    }

    @PostMapping("/return/confirm")
    public String registerReturn(@Valid @RequestParam Long loanId, RedirectAttributes ra) {
        try {
            loanService.registerReturn(loanId);
            ra.addFlashAttribute("toastOk",
                    "Préstamo devuelto correctamente");
            return "redirect:/admin/return";

        } catch (Exception ex) {
            ra.addFlashAttribute("toastErr",
                    (ex.getMessage() != null && !ex.getMessage().isBlank())
                            ? ex.getMessage()
                            : "No se pudo registrar la devolución préstamo.");
            ra.addAttribute("loanId", loanId);
            return "redirect:/admin/return";
        }
    }


}

