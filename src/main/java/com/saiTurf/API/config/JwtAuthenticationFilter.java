package com.saiTurf.API.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, 
                                   @Qualifier("customUserDetailsService") UserDetailsService userDetailsService) { 
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        try {
            // ✅ Skip JWT authentication for login & register
        	String requestURI = request.getRequestURI();
            if (requestURI.startsWith("/api/login") || requestURI.startsWith("/api/register") || requestURI.startsWith("/api/test") || requestURI.startsWith("/api/images")) {
                System.out.println("Skipping JWT filter for: " + requestURI); // ✅ Debug
                chain.doFilter(request, response);
                return;
            }

            final String authHeader = request.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                sendErrorResponse(response, "Authorization header missing");
                return;
            }

            final String token = authHeader.substring(7);
            final String username = jwtUtil.extractUsername(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtUtil.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    sendErrorResponse(response, "Invalid token");
                    return;
                }
            }
            chain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            sendErrorResponse(response, "JWT token has expired");
        } catch (SignatureException e) {
            sendErrorResponse(response, "Invalid JWT signature");
        } catch (MalformedJwtException e) {
            sendErrorResponse(response, "Malformed JWT token");
        } catch (IllegalArgumentException e) {
            sendErrorResponse(response, "JWT claims string is empty");
        } catch (Exception e) {
            sendErrorResponse(response, "Authentication error: " + e.getMessage());
        }
    }



    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        if (response.isCommitted()) {
            System.out.println("Response already committed, skipping error response: " + message);
            return;
        }

        response.reset(); // Clear any partially written content
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("error", message);
        responseData.put("status", HttpServletResponse.SC_UNAUTHORIZED);

        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(responseData));
        response.getWriter().flush();
    }

}
