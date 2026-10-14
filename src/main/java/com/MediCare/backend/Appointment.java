package com.MediCare.backend;

import jakarta.persistence.*;

@Entity
@Table(name = "appointments")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false)
    public String doctor;

    public String specialty;

    @Column(nullable = false)
    public String patient;

    @Column(name = "appointment_date", nullable = false)
    public String date;

    @Column(name = "appointment_time", nullable = false)
    public String time;

    @Column(name = "user_id")
    public Long userId;

    public Appointment() {
    }
}
