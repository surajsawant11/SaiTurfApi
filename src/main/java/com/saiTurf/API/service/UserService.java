package com.saiTurf.API.service;

import com.saiTurf.API.model.UserModel;
import com.saiTurf.API.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {  // ❌ Removed `implements UserDetailsService`

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserModel registerUser(String name, String email, String password, UserModel.Role role) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }

        UserModel user = new UserModel();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));  
        user.setRole(role);

        return userRepository.save(user);
    }

    public Optional<UserModel> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean authenticateUser(String email, String rawPassword) {
        Optional<UserModel> userOpt = userRepository.findByEmail(email);
        return userOpt.map(user -> passwordEncoder.matches(rawPassword, user.getPassword())).orElse(false);
    }
}
