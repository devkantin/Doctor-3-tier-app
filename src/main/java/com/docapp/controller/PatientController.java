package com.docapp.controller;

import com.docapp.model.*;
import com.docapp.repository.PatientRepository;
import com.docapp.service.AppointmentService;
import com.docapp.service.DoctorService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/patient")
public class PatientController {

    private final DoctorService      doctorService;
    private final AppointmentService appointmentService;
    private final PatientRepository  patientRepository;

    public PatientController(DoctorService doctorService,
                             AppointmentService appointmentService,
                             PatientRepository patientRepository) {
        this.doctorService      = doctorService;
        this.appointmentService = appointmentService;
        this.patientRepository  = patientRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal User user, Model model) {
        Patient patient = patientRepository.findByUser(user).orElse(null);
        if (patient != null) {
            List<Appointment> appointments = appointmentService.getByPatient(patient);
            model.addAttribute("appointments", appointments);
            model.addAttribute("totalAppointments", appointments.size());
            long pending   = appointments.stream().filter(a -> a.getStatus() == AppointmentStatus.PENDING).count();
            long confirmed = appointments.stream().filter(a -> a.getStatus() == AppointmentStatus.CONFIRMED).count();
            model.addAttribute("pending", pending);
            model.addAttribute("confirmed", confirmed);
        }
        model.addAttribute("patient", patient);
        return "patient/dashboard";
    }

    @GetMapping("/doctors")
    public String doctors(@RequestParam(required = false) String specialization, Model model) {
        List<Doctor> doctors = (specialization != null && !specialization.isBlank())
            ? doctorService.searchDoctors(specialization)
            : doctorService.getAvailableDoctors();
        model.addAttribute("doctors", doctors);
        model.addAttribute("specialization", specialization);
        return "patient/doctors";
    }

    @GetMapping("/book/{doctorId}")
    public String bookPage(@PathVariable Long doctorId,
                           @RequestParam(required = false) String date,
                           Model model) {
        Doctor doctor = doctorService.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));
        model.addAttribute("doctor", doctor);
        model.addAttribute("today", LocalDate.now().toString());

        if (date != null && !date.isBlank()) {
            LocalDate ld = LocalDate.parse(date);
            model.addAttribute("availableSlots", appointmentService.getAvailableSlots(doctor, ld));
            model.addAttribute("selectedDate", date);
        }
        return "patient/book";
    }

    @PostMapping("/book")
    public String book(@AuthenticationPrincipal User user,
                       @RequestParam Long doctorId,
                       @RequestParam String date,
                       @RequestParam String timeSlot,
                       @RequestParam(defaultValue = "") String reason,
                       RedirectAttributes redirect) {
        Doctor doctor = doctorService.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));
        appointmentService.book(user, doctor, LocalDate.parse(date), timeSlot, reason);
        redirect.addFlashAttribute("success", "Appointment booked! You will receive a confirmation shortly.");
        return "redirect:/patient/appointments";
    }

    @GetMapping("/appointments")
    public String appointments(@AuthenticationPrincipal User user, Model model) {
        Patient patient = patientRepository.findByUser(user).orElse(null);
        if (patient != null) {
            model.addAttribute("appointments", appointmentService.getByPatient(patient));
        }
        return "patient/appointments";
    }

    @PostMapping("/appointments/{id}/cancel")
    public String cancel(@PathVariable Long id, RedirectAttributes redirect) {
        appointmentService.updateStatus(id, AppointmentStatus.CANCELLED, "Cancelled by patient");
        redirect.addFlashAttribute("success", "Appointment cancelled.");
        return "redirect:/patient/appointments";
    }
}
