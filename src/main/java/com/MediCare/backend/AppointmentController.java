package com.MediCare.backend;

import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(originPatterns = {"http://localhost:[*]", "http://127.0.0.1:[*]"}, allowCredentials = "true")
public class AppointmentController {

    private final AppointmentRepository repository;

    public AppointmentController(AppointmentRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public ResponseEntity<?> getAppointments(HttpSession session) {
        Long userId = getUserId(session);
        if (userId == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("message", "Please log in."));
        }

        List<Appointment> records =
                repository.findByUserIdOrderByIdDesc(userId);
        return ResponseEntity.ok(records);
    }

    @PostMapping
    public ResponseEntity<?> bookAppointment(
            @RequestBody Appointment appointment,
            HttpSession session) {

        Long userId = getUserId(session);
        if (userId == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("message", "Please log in."));
        }

        if (appointment == null
                || appointment.patient == null
                || appointment.patient.isBlank()
                || appointment.doctor == null
                || appointment.doctor.isBlank()
                || appointment.date == null
                || appointment.date.isBlank()
                || appointment.time == null
                || appointment.time.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Complete all appointment fields."));
        }

        appointment.id = null;
        appointment.userId = userId;

        return ResponseEntity.ok(repository.save(appointment));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelAppointment(
            @PathVariable Long id,
            HttpSession session) {

        Long userId = getUserId(session);
        if (userId == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("message", "Please log in."));
        }

        Appointment appointment = repository.findById(id).orElse(null);

        if (appointment == null || !userId.equals(appointment.userId)) {
            return ResponseEntity.notFound().build();
        }

        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private Long getUserId(HttpSession session) {
        Object value = session.getAttribute("userId");
        return value instanceof Long id ? id : null;
    }
}
