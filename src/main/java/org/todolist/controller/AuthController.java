package org.todolist.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.todolist.dto.AuthRequest;
import org.todolist.dto.AuthResponse;
import org.todolist.dto.RegisterRequest;
import org.todolist.service.CustomUserService;

@RestController
@RequestMapping("/api/v1.0")
public class AuthController {

    private final CustomUserService customUserService;

    public AuthController(CustomUserService customUserService) {
        this.customUserService = customUserService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        try {
            AuthResponse resp = customUserService.register(req);
            return ResponseEntity.ok(resp);
        } catch (IllegalArgumentException ex) {
            String msg = ex.getMessage();
            if ("username_taken".equals(msg) || "email_taken".equals(msg)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(msg);
            }
            return ResponseEntity.badRequest().body(msg);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("registration_error");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest req) {
        try {
            AuthResponse resp = customUserService.login(req);
            return ResponseEntity.ok(resp);
        } catch (IllegalArgumentException ex) {
            String msg = ex.getMessage();
            if ("invalid_credentials".equals(msg)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(msg);
            }
            return ResponseEntity.badRequest().body(msg);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("login_error");
        }
    }
}
