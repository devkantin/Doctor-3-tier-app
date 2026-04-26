package com.docapp.service;

import com.docapp.model.Doctor;
import com.docapp.model.Patient;
import com.docapp.model.Role;
import com.docapp.model.User;
import com.docapp.repository.DoctorRepository;
import com.docapp.repository.PatientRepository;
import com.docapp.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       DoctorRepository doctorRepository,
                       PatientRepository patientRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }

    @Transactional
    public User registerPatient(String name, String email, String password, String phone,
                                String gender, String bloodGroup, String address) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already registered");
        }
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(Role.PATIENT);
        userRepository.save(user);

        Patient patient = new Patient();
        patient.setUser(user);
        patient.setPhone(phone);
        patient.setGender(gender);
        patient.setBloodGroup(bloodGroup);
        patient.setAddress(address);
        patientRepository.save(patient);

        return user;
    }

    @Transactional
    public User registerDoctor(String name, String email, String password,
                               String specialization, String qualification,
                               int experience, double fee, String phone, String bio) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already registered");
        }
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(Role.DOCTOR);
        userRepository.save(user);

        Doctor doctor = new Doctor();
        doctor.setUser(user);
        doctor.setSpecialization(specialization);
        doctor.setQualification(qualification);
        doctor.setExperience(experience);
        doctor.setConsultationFee(fee);
        doctor.setPhone(phone);
        doctor.setBio(bio);
        doctorRepository.save(doctor);

        return user;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public long countUsers() {
        return userRepository.count();
    }
}
