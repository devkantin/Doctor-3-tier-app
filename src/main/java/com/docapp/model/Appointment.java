package com.docapp.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Column(name = "appointment_date", nullable = false)
    private LocalDate appointmentDate;

    @Column(name = "time_slot", nullable = false)
    private String timeSlot;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status = AppointmentStatus.PENDING;

    private String reason;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }

    // ── Getters / Setters ────────────────────────────────────
    public Long getId()                             { return id; }
    public void setId(Long id)                      { this.id = id; }
    public Patient getPatient()                     { return patient; }
    public void setPatient(Patient patient)         { this.patient = patient; }
    public Doctor getDoctor()                       { return doctor; }
    public void setDoctor(Doctor doctor)            { this.doctor = doctor; }
    public LocalDate getAppointmentDate()           { return appointmentDate; }
    public void setAppointmentDate(LocalDate d)     { this.appointmentDate = d; }
    public String getTimeSlot()                     { return timeSlot; }
    public void setTimeSlot(String timeSlot)        { this.timeSlot = timeSlot; }
    public AppointmentStatus getStatus()            { return status; }
    public void setStatus(AppointmentStatus s)      { this.status = s; }
    public String getReason()                       { return reason; }
    public void setReason(String reason)            { this.reason = reason; }
    public String getNotes()                        { return notes; }
    public void setNotes(String notes)              { this.notes = notes; }
    public LocalDateTime getCreatedAt()             { return createdAt; }
}
