package com.sudhar.urlshortener.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import com.sudhar.urlshortener.entity.User;
import com.sudhar.urlshortener.repository.UserRepository;
import com.sudhar.urlshortener.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepo;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder encoder;

    public AuthController(UserRepository userRepo, JwtUtil jwtUtil, PasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.jwtUtil = jwtUtil;
        this.encoder = encoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {

        user.setPassword(encoder.encode(user.getPassword()));

        if (user.getRole() == null) {
            user.setRole("ROLE_USER");
        }

        userRepo.save(user);

        return ResponseEntity.ok("User registered");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {

        Optional<User> dbUser = userRepo.findByUsername(user.getUsername());

        if (dbUser.isEmpty() ||
                !encoder.matches(user.getPassword(), dbUser.get().getPassword())) {
            return ResponseEntity.badRequest().body("Invalid credentials");
        }

        String token = jwtUtil.generateToken(
                dbUser.get().getUsername(),
                dbUser.get().getRole()
        );

        return ResponseEntity.ok(token);
    }
}
