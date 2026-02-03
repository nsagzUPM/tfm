package com.upm.library.advice;

import com.upm.library.domain.Role;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice
public class GlobalModelAttributes {

    @ModelAttribute("userName")
    public String userName(@AuthenticationPrincipal OidcUser user) {
        if (user == null) return null;
        String name = user.getFullName();
        if (name == null || name.isBlank()) name = user.getEmail();
        return name;
    }

    @ModelAttribute("role")
    public String role(@AuthenticationPrincipal OidcUser user) {
        if (user == null) return null;
        Object groups = user.getClaims().get("cognito:groups");
        if (groups instanceof java.util.List<?> list && !list.isEmpty()) {
            return String.valueOf(list.get(0));
        }
        return "USER";
    }

    @ModelAttribute("isLibrarian")
    public boolean isLibrarian(@AuthenticationPrincipal OidcUser user) {
        if (user == null) return false;
        user.getClaims().get("cognito:groups");
        Object groups = user.getClaims().get("cognito:groups");
        if (groups instanceof java.util.List<?> list && !list.isEmpty()) {
            return list.stream().anyMatch(a -> String.valueOf(a).equals("LIBRARIAN"));
        }
        return false;
    }
}
