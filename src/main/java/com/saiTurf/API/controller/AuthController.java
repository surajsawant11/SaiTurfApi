package com.saiTurf.API.controller;

import java.util.HashMap;
import java.util.Map;
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
    	Map<String, String> data = new HashMap();
        System.out.println("Received email: " + request.getEmail());
        System.out.println("Received password: " + request.getPassword());
        
//        PasswordEncoder encoder = new BCryptPasswordEncoder();
//        System.out.println(encoder.encode("password123"));

        Optional<UserModel> userOpt = userService.findByEmail(request.getEmail());
        
        if (userOpt.isEmpty()) {
//            System.out.println("❌ User not found!");
        	data.put("MSG","User not found");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(data);
        }

        UserModel user = userOpt.get();
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            System.out.println("❌ Password mismatch!");
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            data.put("MSG","Invalid Password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(data);
        }

//        System.out.println("✅ User authenticated!");
//        return ResponseEntity.ok("Login successful");
        String token = jwtService.generateToken(user);
        data.put("AccessToken", token);
        return ResponseEntity.ok(data);
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRequest request) {
    	Map<String, String> data = new HashMap();
        if (userService.findByEmail(request.getEmail()).isPresent()) {
        	data.put("MSG","Email Already Exists");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(data);
        }

        UserModel user = new UserModel();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // Hashing password
        user.setRole(request.getRole()); // Assuming role is passed as "USER" or "ADMIN"

        userService.registerUser(user.getName(), user.getEmail(), request.getPassword(), request.getRole());
        data.put("MSG","User registered successfully");
        return ResponseEntity.ok(data);
    }

}
