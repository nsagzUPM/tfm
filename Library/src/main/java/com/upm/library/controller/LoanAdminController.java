package com.upm.library.controller;

import com.upm.library.dto.admin.CreateLoanRequest;
import com.upm.library.dto.admin.LoanAdminDto;
import com.upm.library.dto.admin.RegisterReturnRequest;
import com.upm.library.dto.admin.UserSummaryDto;
import com.upm.library.dto.catalog.CopyDetailsDto;
import com.upm.library.exception.NotFoundException;
import com.upm.library.repository.CopyRepository;
import com.upm.library.repository.UserRepository;
import com.upm.library.service.LoanService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class LoanAdminController {

    private final LoanService loanService;
    private final UserRepository userRepository;
    private final CopyRepository copyRepository;

    public LoanAdminController(LoanService loanService, UserRepository userRepository, CopyRepository copyRepository) {
        this.loanService = loanService;
        this.userRepository = userRepository;
        this.copyRepository = copyRepository;
    }

    @GetMapping("/loan")
    public String createLoan(@RequestParam(required = false, name = "userQ") String userQ,
                             @RequestParam(required = false, name = "userId") Long userId,
                             @RequestParam(required = false, name = "copyCode") Long copyId, Model model) {
        model.addAttribute("userQ", userQ);
        model.addAttribute("users", (userQ == null || userQ.isBlank()) ? List.of() : loanService.searchUsers(userQ)
                .stream().map(UserSummaryDto::from).toList());
        if(userId != null) {
            model.addAttribute("selectedUser", UserSummaryDto.from(userRepository.findById(userId)
                    .orElseThrow(() -> new NotFoundException("User not found."))));
        }
        if(copyId != null){
            model.addAttribute("copy", CopyDetailsDto.from(copyRepository.findById(copyId)
                    .orElseThrow(() -> new NotFoundException("User not found."))));
        }
        return "admin-loan";
    }

    @PostMapping("/loan/select")
    public String createLoan(@RequestParam(required = false, name = "userQ") String userQ,
                             @RequestParam(name = "userId") Long userId, Model model) {
        model.addAttribute("userQ", userQ);
        model.addAttribute("users", (userQ == null || userQ.isBlank()) ? List.of() : loanService.searchUsers(userQ)
                .stream().map(UserSummaryDto::from).toList());
        model.addAttribute("selectedUser", UserSummaryDto.from(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found."))));
        return "admin-loan";
    }


    @PostMapping("/loans")
    public LoanAdminDto createLoan(@Valid @RequestBody CreateLoanRequest req) {
        return LoanAdminDto.from(loanService.createLoan(req.userId(), req.copyId()));
    }

    @PostMapping("/returns")
    public void registerReturn(@Valid @RequestBody RegisterReturnRequest req) {
        loanService.registerReturn(req.copyId());
    }
}

