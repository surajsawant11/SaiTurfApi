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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.saiTurf.API.dto.AuthRequest;
import com.saiTurf.API.model.UserModel;
import com.saiTurf.API.model.UserModel.Role;
import com.saiTurf.API.service.JwtService;
import com.saiTurf.API.service.UserService;


@RestController
@RequestMapping("/api")
public class AuthController {
	@GetMapping("/test")
    public String test() {
		System.out.println("testcalled");
		
//		 String key = "iC3AXmvGAZLw3jcA9YBnsB80ay2h7ZlWDLrSqP4ZJGM=";
//	        byte[] decodedKey = Base64.getDecoder().decode(key);
//	        System.out.println(new String(decodedKey));
        return "api is started";	
	}
	

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
    	Map<String, Object> data = new HashMap();
        System.out.println("Received email: " + request.getUsername());
        System.out.println("Received password: " + request.getPassword());
        
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println(encoder.encode(request.getPassword()));

        Optional<UserModel> userOpt = userService.findByUserName(request.getUsername());
        
        if (userOpt.isEmpty()) {
//            System.out.println("❌ User not found!");
        	data.put("message","User not found");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(data);
        }

        UserModel user = userOpt.get();
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            System.out.println("❌ Password mismatch!");
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            data.put("message","Invalid Password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(data);
        }

//        System.out.println("✅ User authenticated!");
//        return ResponseEntity.ok("Login successful");
        String token = jwtService.generateToken(user);
        
        Map<String, Object> userData = new HashMap();
        userData.put("username", user.getUsername());
        userData.put("userid", user.getId());
        userData.put("role", user.getRole());
        
        data.put("user", userData);
        data.put("token", token);
        return ResponseEntity.ok(data);
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserModel request) {
    	Map<String, Object> data = new HashMap();
        if (userService.findByUserName(request.getUsername()).isPresent()) {
        	data.put("message","User Already Exists");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(data);
        }

        UserModel user = new UserModel();
        user.setUserName(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // Hashing password
        if(request.getRole() ==null)
        	user.setRole(Role.USER);
        else
        	user.setRole(request.getRole()); // Assuming role is passed as "USER" or "ADMIN"
        user = userService.registerUser(user.getUsername(), user.getEmail(), request.getPassword(), request.getRole());
        data.put("message",user.getUsername()+" registered successfully");
        data.put("user",user);
        return ResponseEntity.status(HttpStatus.CREATED).body(data);
    }

}
