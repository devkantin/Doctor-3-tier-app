package com.docapp.controller;

import com.docapp.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String name,
                           @RequestParam String email,
                           @RequestParam String password,
                           @RequestParam(defaultValue = "") String phone,
                           @RequestParam(defaultValue = "Other") String gender,
                           @RequestParam(defaultValue = "") String bloodGroup,
                           @RequestParam(defaultValue = "") String address,
                           RedirectAttributes redirect) {
        try {
            userService.registerPatient(name, email, password, phone, gender, bloodGroup, address);
            redirect.addFlashAttribute("success", "Account created! Please log in.");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }
}
