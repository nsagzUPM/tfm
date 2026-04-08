package com.upm.library.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
class GeneralController {

    @GetMapping
    public String home() {
        return "redirect:/menu";
    }

    @GetMapping("/menu")
    public String menu(Model model) {
        model.addAttribute("title", "Menú principal");
        return "menu";
    }

}
