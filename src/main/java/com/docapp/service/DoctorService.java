package com.docapp.service;

import com.docapp.model.Doctor;
import com.docapp.model.User;
import com.docapp.repository.DoctorRepository;
import net.rubyeye.xmemcached.MemcachedClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DoctorService {

    private static final Logger log = LoggerFactory.getLogger(DoctorService.class);
    private static final String CACHE_KEY_ALL = "doctors:all";
    private static final int    CACHE_TTL     = 300; // 5 minutes

    private final DoctorRepository doctorRepository;
    private final MemcachedClient  memcachedClient;

    public DoctorService(DoctorRepository doctorRepository,
                         MemcachedClient memcachedClient) {
        this.doctorRepository = doctorRepository;
        this.memcachedClient  = memcachedClient;
    }

    public List<Doctor> getAvailableDoctors() {
        // Try Memcached first (ElastiCache in production)
        if (memcachedClient != null) {
            try {
                @SuppressWarnings("unchecked")
                List<Doctor> cached = memcachedClient.get(CACHE_KEY_ALL);
                if (cached != null) {
                    log.debug("Cache HIT — doctors:all");
                    return cached;
                }
            } catch (Exception e) {
                log.warn("Memcached GET failed: {}", e.getMessage());
            }
        }

        List<Doctor> doctors = doctorRepository.findByAvailableTrue();

        if (memcachedClient != null) {
            try {
                memcachedClient.set(CACHE_KEY_ALL, CACHE_TTL, doctors);
                log.debug("Cache SET — doctors:all ({} items)", doctors.size());
            } catch (Exception e) {
                log.warn("Memcached SET failed: {}", e.getMessage());
            }
        }
        return doctors;
    }

    public List<Doctor> searchDoctors(String specialization) {
        return doctorRepository.findBySpecializationContainingIgnoreCaseAndAvailableTrue(specialization);
    }

    public Optional<Doctor> findById(Long id) {
        return doctorRepository.findById(id);
    }

    public Optional<Doctor> findByUser(User user) {
        return doctorRepository.findByUser(user);
    }

    public Doctor save(Doctor doctor) {
        evictCache();
        return doctorRepository.save(doctor);
    }

    public long count() {
        return doctorRepository.count();
    }

    public List<Doctor> findAll() {
        return doctorRepository.findAll();
    }

    private void evictCache() {
        if (memcachedClient != null) {
            try {
                memcachedClient.delete(CACHE_KEY_ALL);
            } catch (Exception e) {
                log.warn("Memcached DELETE failed: {}", e.getMessage());
            }
        }
    }
}
