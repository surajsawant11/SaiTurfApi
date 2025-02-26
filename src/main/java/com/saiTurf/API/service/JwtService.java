//package com.saiTurf.API.service;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import io.jsonwebtoken.security.Keys;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.security.Key;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.function.Function;
//
//@Service
//@Transactional
//public class JwtService {
//
//    private final String SECRET_KEY = "aaaaaaaaaaaaaaaaaaaaaabbbbbbbbbbbbbbbbbbDDDDDDDDDDDDDDDDDCCCCCFFFFF"; 
//
//    /**
//     * ✅ Extract username from token
//     */
//    public String extractUsername(String token) {
//        return extractClaim(token, Claims::getSubject);
//    }
//
//    /**
//     * ✅ Extract user ID from token
//     */
//    public Long extractUserId(String token) {
//        return extractClaim(token, claims -> claims.get("userId", Long.class)); // Extract userId
//    }
//
//    /**
//     * ✅ Extract role from token
//     */
//    public String extractRole(String token) {
//        return extractClaim(token, claims -> claims.get("role", String.class)); // Extract role
//    }
//
//    /**
//     * ✅ Extract a specific claim from token
//     */
//    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
//        Claims claims = extractAllClaims(token);
//        return claimsResolver.apply(claims);
//    }
//
//    /**
//     * ✅ Generate token for user with userId and role
//     */
//    public String generateToken(UserDetails userDetails, Long userId, String role) {
//        Map<String, Object> claims = new HashMap<>();
//        claims.put("userId", userId); // Store userId
//        claims.put("role", role); // Store role
//        return generateToken(claims, userDetails);
//    }
//
//    /**
//     * ✅ Generate token with extra claims
//     */
//    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
//        return Jwts.builder()
//                .setClaims(extraClaims)
//                .setSubject(userDetails.getUsername()) // Store username
//                .setIssuedAt(new Date(System.currentTimeMillis()))
//                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 hours
//                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
//                .compact();
//    }
//
//    /**
//     * ✅ Validate token
//     */
//    public boolean validateToken(String token, UserDetails userDetails) {
//        String username = extractUsername(token);
//        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
//    }
//
//    /**
//     * ✅ Check if token is expired
//     */
//    private boolean isTokenExpired(String token) {
//        return extractClaim(token, Claims::getExpiration).before(new Date());
//    }
//
//    /**
//     * ✅ Extract all claims from token
//     */
//    private Claims extractAllClaims(String token) {
//        return Jwts.parserBuilder()
//                .setSigningKey(getSignInKey())
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//    }
//
//    /**
//     * ✅ Get signing key
//     */
//    private Key getSignInKey() {
//        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes()); // Correct way to get the key
//    }
//}
