package com.saiTurf.API.service;

import com.saiTurf.API.model.UserModel;
import com.saiTurf.API.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class UserService {  // ❌ Removed `implements UserDetailsService`

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserModel save(UserModel user) {
        return userRepository.save(user);
    }

    public Optional<UserModel> findByUserName(String userName) {
        return userRepository.findByUsername(userName);
    }

    public boolean authenticateUser(String userName, String rawPassword) {
        Optional<UserModel> userOpt = userRepository.findByUsername(userName);
        return userOpt.map(user -> passwordEncoder.matches(rawPassword, user.getPassword())).orElse(false);
    }
}
