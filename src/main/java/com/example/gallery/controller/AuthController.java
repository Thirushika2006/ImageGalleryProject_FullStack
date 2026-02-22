package com.example.gallery.controller;

import com.example.gallery.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @GetMapping("/login-page")
    public org.springframework.web.servlet.ModelAndView loginPage() {
        return new org.springframework.web.servlet.ModelAndView("forward:/login.html");
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(
            @RequestParam String username,
            @RequestParam String password) {
        return ResponseEntity.ok(authService.register(username, password));
    }

    // ✅ Register admin (use this once to create your admin account)
    @PostMapping("/register-admin")
    public ResponseEntity<String> registerAdmin(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String secretKey) {
        // Simple secret key to prevent anyone from creating admin
        if (!"ADMIN_SECRET_2024".equals(secretKey)) {
            return ResponseEntity.status(403).body("Invalid secret key!");
        }
        return ResponseEntity.ok(authService.registerAdmin(username, password));
    }

    @GetMapping("/me")
    public ResponseEntity<String> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Not logged in");
        }
        return ResponseEntity.ok(authentication.getName());
    }

    // ✅ Get current user role (for showing/hiding admin button)
    @GetMapping("/role")
    public ResponseEntity<String> getCurrentRole(Authentication authentication) {
        if (authentication == null) return ResponseEntity.status(401).body("Not logged in");
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        return ResponseEntity.ok(isAdmin ? "ADMIN" : "USER");
    }
}