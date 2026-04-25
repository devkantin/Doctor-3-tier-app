package com.docapp.controller;

import com.docapp.model.AppointmentStatus;
import com.docapp.service.AppointmentService;
import com.docapp.service.DoctorService;
import com.docapp.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService        userService;
    private final DoctorService      doctorService;
    private final AppointmentService appointmentService;

    public AdminController(UserService userService, DoctorService doctorService,
                           AppointmentService appointmentService) {
        this.userService        = userService;
        this.doctorService      = doctorService;
        this.appointmentService = appointmentService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalUsers",        userService.countUsers());
        model.addAttribute("totalDoctors",      doctorService.count());
        model.addAttribute("totalAppointments", appointmentService.countAll());
        model.addAttribute("pending",           appointmentService.countByStatus(AppointmentStatus.PENDING));
        model.addAttribute("confirmed",         appointmentService.countByStatus(AppointmentStatus.CONFIRMED));
        model.addAttribute("completed",         appointmentService.countByStatus(AppointmentStatus.COMPLETED));
        model.addAttribute("recentAppointments", appointmentService.getAll().stream().limit(8).toList());
        return "admin/dashboard";
    }

    @GetMapping("/doctors")
    public String doctors(Model model) {
        model.addAttribute("doctors", doctorService.findAll());
        return "admin/doctors";
    }

    @PostMapping("/doctors/{id}/toggle")
    public String toggleDoctor(@PathVariable Long id, RedirectAttributes redirect) {
        doctorService.findById(id).ifPresent(d -> {
            d.setAvailable(!d.isAvailable());
            doctorService.save(d);
        });
        redirect.addFlashAttribute("success", "Doctor status updated.");
        return "redirect:/admin/doctors";
    }

    @PostMapping("/doctors/add")
    public String addDoctor(@RequestParam String name,
                            @RequestParam String email,
                            @RequestParam String password,
                            @RequestParam String specialization,
                            @RequestParam String qualification,
                            @RequestParam int experience,
                            @RequestParam double fee,
                            @RequestParam String phone,
                            @RequestParam String bio,
                            RedirectAttributes redirect) {
        try {
            userService.registerDoctor(name, email, password, specialization,
                                       qualification, experience, fee, phone, bio);
            redirect.addFlashAttribute("success", "Doctor added successfully.");
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/doctors";
    }

    @GetMapping("/appointments")
    public String appointments(Model model) {
        model.addAttribute("appointments", appointmentService.getAll());
        return "admin/appointments";
    }

    @PostMapping("/appointments/{id}/cancel")
    public String cancelAppointment(@PathVariable Long id, RedirectAttributes redirect) {
        appointmentService.updateStatus(id, AppointmentStatus.CANCELLED, "Cancelled by admin");
        redirect.addFlashAttribute("success", "Appointment cancelled.");
        return "redirect:/admin/appointments";
    }
}
