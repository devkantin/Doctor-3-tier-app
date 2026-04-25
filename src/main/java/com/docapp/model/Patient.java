package com.docapp.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "patients")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String phone;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    private String gender;

    @Column(name = "blood_group")
    private String bloodGroup;

    private String address;

    // ── Getters / Setters ────────────────────────────────────
    public Long getId()                        { return id; }
    public void setId(Long id)                 { this.id = id; }
    public User getUser()                      { return user; }
    public void setUser(User user)             { this.user = user; }
    public String getPhone()                   { return phone; }
    public void setPhone(String phone)         { this.phone = phone; }
    public LocalDate getDateOfBirth()          { return dateOfBirth; }
    public void setDateOfBirth(LocalDate d)    { this.dateOfBirth = d; }
    public String getGender()                  { return gender; }
    public void setGender(String gender)       { this.gender = gender; }
    public String getBloodGroup()              { return bloodGroup; }
    public void setBloodGroup(String bg)       { this.bloodGroup = bg; }
    public String getAddress()                 { return address; }
    public void setAddress(String address)     { this.address = address; }
}
