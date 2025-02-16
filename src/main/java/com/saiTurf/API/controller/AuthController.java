package com.saiTurf.API.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.saiTurf.API.model.dto.AuthRequest;
import com.saiTurf.API.model.UserModel;
import com.saiTurf.API.service.JwtService;
import com.saiTurf.API.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userService;
    }
  

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        System.out.println("Received email: " + request.getEmail());
        System.out.println("Received password: " + request.getPassword());
        
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println(encoder.encode("password123"));

        Optional<UserModel> userOpt = userService.findByEmail(request.getEmail());
        
        if (userOpt.isEmpty()) {
            System.out.println("❌ User not found!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }

        UserModel user = userOpt.get();
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            System.out.println("❌ Password mismatch!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

//        System.out.println("✅ User authenticated!");
//        return ResponseEntity.ok("Login successful");
        String token = jwtService.generateToken(user);
        return ResponseEntity.ok(token);
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRequest request) {
        if (userService.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email already exists");
        }

        UserModel user = new UserModel();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // Hashing password
        user.setRole(request.getRole()); // Assuming role is passed as "USER" or "ADMIN"

        userService.registerUser(user.getName(), user.getEmail(), request.getPassword(), request.getRole());

        return ResponseEntity.ok("User registered successfully");
    }

}
