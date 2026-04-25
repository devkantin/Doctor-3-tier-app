package com.docapp.messaging;

import java.io.Serializable;

public class AppointmentMessage implements Serializable {

    private Long   appointmentId;
    private String patientName;
    private String patientEmail;
    private String doctorName;
    private String doctorSpecialization;
    private String appointmentDate;
    private String timeSlot;
    private String status;

    public AppointmentMessage() {}

    public AppointmentMessage(Long appointmentId, String patientName, String patientEmail,
                              String doctorName, String doctorSpecialization,
                              String appointmentDate, String timeSlot, String status) {
        this.appointmentId       = appointmentId;
        this.patientName         = patientName;
        this.patientEmail        = patientEmail;
        this.doctorName          = doctorName;
        this.doctorSpecialization = doctorSpecialization;
        this.appointmentDate     = appointmentDate;
        this.timeSlot            = timeSlot;
        this.status              = status;
    }

    public Long   getAppointmentId()          { return appointmentId; }
    public void   setAppointmentId(Long id)   { this.appointmentId = id; }
    public String getPatientName()            { return patientName; }
    public void   setPatientName(String n)    { this.patientName = n; }
    public String getPatientEmail()           { return patientEmail; }
    public void   setPatientEmail(String e)   { this.patientEmail = e; }
    public String getDoctorName()             { return doctorName; }
    public void   setDoctorName(String n)     { this.doctorName = n; }
    public String getDoctorSpecialization()   { return doctorSpecialization; }
    public void   setDoctorSpecialization(String s) { this.doctorSpecialization = s; }
    public String getAppointmentDate()        { return appointmentDate; }
    public void   setAppointmentDate(String d){ this.appointmentDate = d; }
    public String getTimeSlot()               { return timeSlot; }
    public void   setTimeSlot(String t)       { this.timeSlot = t; }
    public String getStatus()                 { return status; }
    public void   setStatus(String s)         { this.status = s; }
}
