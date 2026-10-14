package com.MediCare.backend;

import jakarta.persistence.*;

@Entity
@Table(name = "medicare_users")
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false)
    public String name;

    @Column(nullable = false, unique = true)
    public String email;

    @Column(name = "password_hash", nullable = false)
    public String passwordHash;

    public AppUser() {
    }
}
