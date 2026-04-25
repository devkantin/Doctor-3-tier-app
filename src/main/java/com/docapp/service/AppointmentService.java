package com.docapp.service;

import com.docapp.messaging.AppointmentMessage;
import com.docapp.model.*;
import com.docapp.repository.AppointmentRepository;
import com.docapp.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService {

    private static final List<String> TIME_SLOTS = Arrays.asList(
        "09:00 AM", "09:30 AM", "10:00 AM", "10:30 AM",
        "11:00 AM", "11:30 AM", "02:00 PM", "02:30 PM",
        "03:00 PM", "03:30 PM", "04:00 PM", "04:30 PM"
    );

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository     patientRepository;
    private final NotificationService   notificationService;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              PatientRepository patientRepository,
                              NotificationService notificationService) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository     = patientRepository;
        this.notificationService   = notificationService;
    }

    @Transactional
    public Appointment book(User currentUser, Doctor doctor,
                            LocalDate date, String timeSlot, String reason) {
        Patient patient = patientRepository.findByUser(currentUser)
                .orElseThrow(() -> new IllegalStateException("Patient profile not found"));

        Appointment appt = new Appointment();
        appt.setPatient(patient);
        appt.setDoctor(doctor);
        appt.setAppointmentDate(date);
        appt.setTimeSlot(timeSlot);
        appt.setReason(reason);
        appt.setStatus(AppointmentStatus.PENDING);
        appointmentRepository.save(appt);

        // Publish notification to RabbitMQ (Amazon MQ in production)
        AppointmentMessage msg = new AppointmentMessage(
            appt.getId(),
            patient.getUser().getName(),
            patient.getUser().getEmail(),
            doctor.getUser().getName(),
            doctor.getSpecialization(),
            date.toString(),
            timeSlot,
            AppointmentStatus.PENDING.name()
        );
        notificationService.sendNotification(msg);

        return appt;
    }

    @Transactional
    public void updateStatus(Long id, AppointmentStatus status, String notes) {
        Appointment appt = appointmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));
        appt.setStatus(status);
        if (notes != null && !notes.isBlank()) appt.setNotes(notes);
        appointmentRepository.save(appt);

        AppointmentMessage msg = new AppointmentMessage(
            appt.getId(),
            appt.getPatient().getUser().getName(),
            appt.getPatient().getUser().getEmail(),
            appt.getDoctor().getUser().getName(),
            appt.getDoctor().getSpecialization(),
            appt.getAppointmentDate().toString(),
            appt.getTimeSlot(),
            status.name()
        );
        notificationService.sendNotification(msg);
    }

    public List<Appointment> getByPatient(Patient patient) {
        return appointmentRepository.findByPatientOrderByAppointmentDateDesc(patient);
    }

    public List<Appointment> getByDoctor(Doctor doctor) {
        return appointmentRepository.findByDoctorOrderByAppointmentDateDesc(doctor);
    }

    public List<Appointment> getAll() {
        return appointmentRepository.findAllByOrderByCreatedAtDesc();
    }

    public Optional<Appointment> findById(Long id) {
        return appointmentRepository.findById(id);
    }

    public List<String> getAvailableSlots(Doctor doctor, LocalDate date) {
        List<Appointment> booked = appointmentRepository.findByDoctorAndAppointmentDate(doctor, date);
        List<String> bookedSlots = booked.stream()
                .filter(a -> a.getStatus() != AppointmentStatus.CANCELLED)
                .map(Appointment::getTimeSlot)
                .toList();
        return TIME_SLOTS.stream().filter(s -> !bookedSlots.contains(s)).toList();
    }

    public List<String> getAllSlots() { return TIME_SLOTS; }

    public long countByStatus(AppointmentStatus status) {
        return appointmentRepository.countByStatus(status);
    }

    public long countAll() { return appointmentRepository.count(); }
}
