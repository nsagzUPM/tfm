package com.upm.library.controller.admin;

import com.upm.library.dto.admin.CreateLoanRequest;
import com.upm.library.dto.admin.LoanAdminDto;
import com.upm.library.dto.admin.RegisterReturnRequest;
import com.upm.library.service.LoanService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class LoanAdminController {

    private final LoanService loanService;

    public LoanAdminController(LoanService loanService) {
        this.loanService = loanService;
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

