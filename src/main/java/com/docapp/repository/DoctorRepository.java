package com.docapp.repository;

import com.docapp.model.Doctor;
import com.docapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByUser(User user);
    List<Doctor> findByAvailableTrue();
    List<Doctor> findBySpecializationContainingIgnoreCaseAndAvailableTrue(String specialization);
}
