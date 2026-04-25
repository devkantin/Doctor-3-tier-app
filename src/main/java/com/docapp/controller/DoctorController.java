package com.docapp.controller;

import com.docapp.model.*;
import com.docapp.service.AppointmentService;
import com.docapp.service.DoctorService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/doctor")
public class DoctorController {

    private final DoctorService      doctorService;
    private final AppointmentService appointmentService;

    public DoctorController(DoctorService doctorService, AppointmentService appointmentService) {
        this.doctorService      = doctorService;
        this.appointmentService = appointmentService;
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal User user, Model model) {
        Doctor doctor = doctorService.findByUser(user)
                .orElseThrow(() -> new IllegalStateException("Doctor profile not found"));
        List<Appointment> appointments = appointmentService.getByDoctor(doctor);
        long pending   = appointments.stream().filter(a -> a.getStatus() == AppointmentStatus.PENDING).count();
        long confirmed = appointments.stream().filter(a -> a.getStatus() == AppointmentStatus.CONFIRMED).count();
        long completed = appointments.stream().filter(a -> a.getStatus() == AppointmentStatus.COMPLETED).count();
        model.addAttribute("doctor", doctor);
        model.addAttribute("appointments", appointments.stream().limit(5).toList());
        model.addAttribute("total", appointments.size());
        model.addAttribute("pending", pending);
        model.addAttribute("confirmed", confirmed);
        model.addAttribute("completed", completed);
        return "doctor/dashboard";
    }

    @GetMapping("/appointments")
    public String appointments(@AuthenticationPrincipal User user,
                               @RequestParam(required = false) String status,
                               Model model) {
        Doctor doctor = doctorService.findByUser(user)
                .orElseThrow(() -> new IllegalStateException("Doctor profile not found"));
        List<Appointment> all = appointmentService.getByDoctor(doctor);
        if (status != null && !status.isBlank()) {
            AppointmentStatus s = AppointmentStatus.valueOf(status.toUpperCase());
            all = all.stream().filter(a -> a.getStatus() == s).toList();
        }
        model.addAttribute("appointments", all);
        model.addAttribute("statusFilter", status);
        return "doctor/appointments";
    }

    @PostMapping("/appointments/{id}/confirm")
    public String confirm(@PathVariable Long id, RedirectAttributes redirect) {
        appointmentService.updateStatus(id, AppointmentStatus.CONFIRMED, null);
        redirect.addFlashAttribute("success", "Appointment confirmed.");
        return "redirect:/doctor/appointments";
    }

    @PostMapping("/appointments/{id}/complete")
    public String complete(@PathVariable Long id,
                           @RequestParam(defaultValue = "") String notes,
                           RedirectAttributes redirect) {
        appointmentService.updateStatus(id, AppointmentStatus.COMPLETED, notes);
        redirect.addFlashAttribute("success", "Appointment marked as completed.");
        return "redirect:/doctor/appointments";
    }

    @PostMapping("/appointments/{id}/noshow")
    public String noShow(@PathVariable Long id, RedirectAttributes redirect) {
        appointmentService.updateStatus(id, AppointmentStatus.NO_SHOW, "Patient did not show up");
        redirect.addFlashAttribute("warning", "Appointment marked as no-show.");
        return "redirect:/doctor/appointments";
    }
}
