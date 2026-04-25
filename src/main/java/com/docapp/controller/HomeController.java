package com.docapp.controller;

import com.docapp.model.Role;
import com.docapp.model.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal User user) {
        if (user.getRole() == Role.ADMIN)   return "redirect:/admin/dashboard";
        if (user.getRole() == Role.DOCTOR)  return "redirect:/doctor/dashboard";
        return "redirect:/patient/dashboard";
    }
}
