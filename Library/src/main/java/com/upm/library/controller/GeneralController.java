package com.upm.library.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/")
class GeneralController {

    @GetMapping
    public String get() {
        return "home page";
    }

    @GetMapping("/logout")
    public String logout() {
        return "Logged out";
    }
}
