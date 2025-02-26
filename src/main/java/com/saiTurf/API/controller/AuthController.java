package com.saiTurf.API.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.saiTurf.API.config.JwtUtil;
import com.saiTurf.API.dto.AuthRequest;
import com.saiTurf.API.model.UserModel;
import com.saiTurf.API.model.UserModel.Role;
import com.saiTurf.API.service.UserService;

@RestController
@RequestMapping("/api")
public class AuthController {

//    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserService userService) {
//        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @GetMapping("/test")
    public String test() {
        return "API is started";
    }

    /**
     * ✅ Login API - Generates JWT Token with userId & role
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        Map<String, Object> data = new HashMap<>();

        Optional<UserModel> userOpt = userService.findByUserName(request.getUsername());

        if (userOpt.isEmpty()) {
            data.put("message", "User not found");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(data);
        }

        UserModel user = userOpt.get();

        // Check if the entered password matches the encoded password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            data.put("message", "Invalid Password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(data);
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(user, user.getId(), user.getRole().name());

        Map<String, Object> userData = new HashMap<>();
        userData.put("username", user.getUsername());
        userData.put("userId", user.getId());
        userData.put("role", user.getRole().name());

        data.put("user", userData);
        data.put("token", token);
        return ResponseEntity.ok(data);
    }


    /**
     * ✅ Register API - Saves user and returns a success message
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserModel request) {
        Map<String, Object> data = new HashMap<>();

        if (userService.findByUserName(request.getUsername()).isPresent()) {
            data.put("message", "User Already Exists");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(data);
        }

        // Create new user and encode password
        UserModel user = new UserModel();
        user.setUserName(request.getUsername());
        user.setEmail(request.getEmail());

        // Encrypt the password using BCrypt
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        System.out.println("Encoded Password: " + encodedPassword);  // For debugging (optional)

        user.setPassword(encodedPassword);  // Save the encoded password
        user.setRole(request.getRole() == null ? Role.USER : request.getRole());
        user = userService.save(user);

        data.put("message", user.getUsername() + " registered successfully");
        data.put("user", user);
        return ResponseEntity.status(HttpStatus.CREATED).body(data);
    }


}
