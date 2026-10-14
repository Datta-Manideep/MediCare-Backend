package com.MediCare.backend;

import java.util.Locale;
import java.util.Map;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(originPatterns = {"http://localhost:[*]", "http://127.0.0.1:[*]"}, allowCredentials = "true")
public class AuthController {

    private final AppUserRepository users;
    private final AppointmentRepository appointments;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthController(
            AppUserRepository users,
            AppointmentRepository appointments) {
        this.users = users;
        this.appointments = appointments;
    }

    @PostMapping("/register")
    @Transactional
    public ResponseEntity<?> register(
            @RequestBody RegisterRequest request,
            HttpSession session) {

        if (request == null || request.name == null
                || request.email == null || request.password == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Complete all registration fields."));
        }

        String name = request.name.trim();
        String email = request.email.trim().toLowerCase(Locale.ROOT);

        if (name.isEmpty() || email.isEmpty() || !email.contains("@")) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Enter a valid name and email."));
        }

        if (request.password.length() < 8) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Password must contain at least 8 characters."));
        }

        if (users.existsByEmailIgnoreCase(email)) {
            return ResponseEntity.status(409)
                    .body(Map.of("message", "This email is already registered. Please log in."));
        }

        AppUser user = new AppUser();
        user.name = name;
        user.email = email;
        user.passwordHash = encoder.encode(request.password);
        user = users.save(user);

        // Attach earlier unowned demo appointments to the first account.
        appointments.assignUnownedAppointments(user.id);

        session.setAttribute("userId", user.id);
        session.setAttribute("userName", user.name);
        session.setAttribute("userEmail", user.email);

        return ResponseEntity.ok(Map.of(
                "id", user.id,
                "name", user.name,
                "email", user.email
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest request,
            HttpSession session) {

        if (request == null || request.email == null
                || request.password == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Enter your email and password."));
        }

        String email = request.email.trim().toLowerCase(Locale.ROOT);
        AppUser user = users.findByEmailIgnoreCase(email).orElse(null);

        if (user == null || !encoder.matches(request.password, user.passwordHash)) {
            return ResponseEntity.status(401)
                    .body(Map.of("message", "Incorrect email or password."));
        }

        session.setAttribute("userId", user.id);
        session.setAttribute("userName", user.name);
        session.setAttribute("userEmail", user.email);

        return ResponseEntity.ok(Map.of(
                "id", user.id,
                "name", user.name,
                "email", user.email
        ));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(HttpSession session) {
        Object id = session.getAttribute("userId");

        if (!(id instanceof Long userId)) {
            return ResponseEntity.status(401)
                    .body(Map.of("message", "Please log in."));
        }

        return ResponseEntity.ok(Map.of(
                "id", userId,
                "name", session.getAttribute("userName"),
                "email", session.getAttribute("userEmail")
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.noContent().build();
    }

    public static class RegisterRequest {
        public String name;
        public String email;
        public String password;
    }

    public static class LoginRequest {
        public String email;
        public String password;
    }
}
