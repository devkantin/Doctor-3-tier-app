package com.docapp.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "doctors")
public class Doctor implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String specialization;
    private String qualification;
    private int experience;

    @Column(name = "consultation_fee")
    private double consultationFee;

    private String phone;

    @Column(columnDefinition = "TEXT")
    private String bio;

    private boolean available = true;

    // ── Getters / Setters ────────────────────────────────────
    public Long getId()                              { return id; }
    public void setId(Long id)                       { this.id = id; }
    public User getUser()                            { return user; }
    public void setUser(User user)                   { this.user = user; }
    public String getSpecialization()                { return specialization; }
    public void setSpecialization(String s)          { this.specialization = s; }
    public String getQualification()                 { return qualification; }
    public void setQualification(String q)           { this.qualification = q; }
    public int getExperience()                       { return experience; }
    public void setExperience(int e)                 { this.experience = e; }
    public double getConsultationFee()               { return consultationFee; }
    public void setConsultationFee(double f)         { this.consultationFee = f; }
    public String getPhone()                         { return phone; }
    public void setPhone(String phone)               { this.phone = phone; }
    public String getBio()                           { return bio; }
    public void setBio(String bio)                   { this.bio = bio; }
    public boolean isAvailable()                     { return available; }
    public void setAvailable(boolean available)      { this.available = available; }
}
