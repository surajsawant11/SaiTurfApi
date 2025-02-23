/*
 * package com.saiTurf.API.service;
 * 
 * import org.springframework.security.authentication.AuthenticationManager;
 * import org.springframework.security.authentication.
 * UsernamePasswordAuthenticationToken; import
 * org.springframework.security.core.Authentication; import
 * org.springframework.security.core.userdetails.UserDetails; import
 * org.springframework.stereotype.Service;
 * 
 * import com.saiTurf.API.dto.AuthRequest; import
 * com.saiTurf.API.dto.AuthResponse; import
 * com.saiTurf.API.repository.UserRepository;
 * 
 * @Service public class AuthService {
 * 
 * private final AuthenticationManager authenticationManager; private final
 * JwtService jwtService; private final UserRepository userRepository;
 * 
 * public AuthService(AuthenticationManager authenticationManager, JwtService
 * jwtService, UserRepository userRepository) { this.authenticationManager =
 * authenticationManager; this.jwtService = jwtService; this.userRepository =
 * userRepository; }
 * 
 * public AuthResponse authenticate(AuthRequest authRequest) { Authentication
 * authentication = authenticationManager.authenticate( new
 * UsernamePasswordAuthenticationToken(authRequest.getEmail(),
 * authRequest.getPassword()) );
 * 
 * UserDetails userDetails = (UserDetails) authentication.getPrincipal(); String
 * jwtToken = jwtService.generateToken(userDetails);
 * 
 * return new AuthResponse(jwtToken); } }
 */
