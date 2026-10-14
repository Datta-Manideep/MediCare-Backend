package com.MediCare.backend;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/doctors")
@CrossOrigin(origins = "http://localhost:5173")
public class DoctorController {

    @GetMapping
    public List<Doctor> getDoctors() {
        return List.of(
            new Doctor(1L, "Dr. Arun Kumar", "General Physician"),
            new Doctor(2L, "Dr. Priya Sharma", "Cardiologist"),
            new Doctor(3L, "Dr. Rahul Reddy", "Dermatologist"),
            new Doctor(4L, "Dr. Ananya Rao", "Pediatrician"),
            new Doctor(5L, "Dr. Kiran Kumar", "General Physician")
        );
    }

    public static class Doctor {
        public Long id;
        public String name;
        public String specialty;

        public Doctor(Long id, String name, String specialty) {
            this.id = id;
            this.name = name;
            this.specialty = specialty;
        }
    }
}