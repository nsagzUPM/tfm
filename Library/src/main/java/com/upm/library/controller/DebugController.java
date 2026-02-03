package com.upm.library.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/debug")
class DebugController {

    @GetMapping("/debug/roles")
    @ResponseBody
    public Object debugRoles(Authentication authentication) {
        return authentication.getAuthorities();
    }


}
