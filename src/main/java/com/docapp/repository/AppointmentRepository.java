package com.docapp.repository;

import com.docapp.model.Appointment;
import com.docapp.model.AppointmentStatus;
import com.docapp.model.Doctor;
import com.docapp.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatientOrderByAppointmentDateDesc(Patient patient);
    List<Appointment> findByDoctorOrderByAppointmentDateDesc(Doctor doctor);
    List<Appointment> findByDoctorAndAppointmentDate(Doctor doctor, LocalDate date);
    long countByStatus(AppointmentStatus status);

    @Query("SELECT a FROM Appointment a ORDER BY a.createdAt DESC")
    List<Appointment> findAllByOrderByCreatedAtDesc();
}
