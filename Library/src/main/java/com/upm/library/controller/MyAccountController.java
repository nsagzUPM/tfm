package com.upm.library.controller;

import com.upm.library.dto.account.MyAccountDto;
import com.upm.library.service.LoanService;
import com.upm.library.service.MyAccountService;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/my-account")
public class MyAccountController {

    private final MyAccountService myAccountService;
    private final LoanService loanService;

    public MyAccountController(MyAccountService myAccountService, LoanService loanService) {
        this.myAccountService = myAccountService;
        this.loanService = loanService;
    }

    @GetMapping
    public MyAccountDto get(Authentication auth) {
        String subject = ((Jwt) auth.getPrincipal()).getSubject();
        return MyAccountDto.from(myAccountService.getAccountData(subject));
    }

    @PostMapping("/loans/{loanId}/renew")
    public void renew(Authentication auth, @PathVariable Long loanId) {
        String subject = ((Jwt) auth.getPrincipal()).getSubject();
        loanService.renewLoan(subject, loanId);
    }
}
