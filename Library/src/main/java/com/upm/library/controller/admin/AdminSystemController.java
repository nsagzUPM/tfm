package com.upm.library.controller.admin;

import com.upm.library.service.SystemConfigService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import com.upm.library.dto.admin.SystemConfigForm;
import jakarta.validation.Valid;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/config")
public class AdminSystemController {

    private final SystemConfigService service;

    public AdminSystemController(SystemConfigService service) {
        this.service = service;
    }

    @GetMapping
    public String view(Model model) {
        if (!model.containsAttribute("form")) {
            var cfg = service.getOrCreateDefault();
            model.addAttribute("form", service.toForm(cfg));
        }
        return "admin-config";
    }

    @PostMapping
    public String save(@Valid @ModelAttribute("form") SystemConfigForm form,
                       BindingResult br,
                       RedirectAttributes ra) {

        if (br.hasErrors()) {
            ra.addFlashAttribute("toastErr", "Revisa los valores. Deben ser números válidos (mínimos: 1; sanción puede ser 0).");
            ra.addFlashAttribute("org.springframework.validation.BindingResult.form", br);
            ra.addFlashAttribute("form", form);
            return "redirect:/admin/config";
        }

        service.update(form);
        ra.addFlashAttribute("toastOk", "Cambios guardados ✅");
        return "redirect:/admin/config";
    }
}
