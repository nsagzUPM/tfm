package com.upm.library.controller;

import com.upm.library.dto.account.MyAccountDto;
import com.upm.library.service.LoanService;
import com.upm.library.service.MyAccountService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/my-account")
public class MyAccountController {

    private final MyAccountService myAccountService;
    private final LoanService loanService;

    public MyAccountController(MyAccountService myAccountService, LoanService loanService) {
        this.myAccountService = myAccountService;
        this.loanService = loanService;
    }

    @GetMapping
    public String get(@AuthenticationPrincipal OidcUser user, Model model) {
        model.addAttribute("myAccount", MyAccountDto.from(myAccountService.getActiveAccountData(user.getEmail())));
        return "my-account";
    }

    @PostMapping("/loans/{loanId}/renew")
    public void renew(Authentication auth, @PathVariable Long loanId) {
        String subject = ((Jwt) auth.getPrincipal()).getSubject();
        loanService.renewLoan(subject, loanId);
    }
}
