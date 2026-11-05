
package com.example.demo;

// Servlet Imports
import jakarta.servlet.*;
import jakarta.servlet.http.*;

// Spring Configuration Imports
import org.springframework.context.annotation.*;
import org.springframework.stereotype.Component;

// Spring Security Imports
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// Filter Imports
import org.springframework.web.filter.OncePerRequestFilter;

// CORS Imports
import org.springframework.web.cors.*;

// Java Imports
import java.io.IOException;
import java.util.*;

// ==========================================
// 1. SECURITY CONFIGURATION
// ==========================================

@Configuration
class SecurityConfig {

    // Password Encoder Bean
    @Bean
    PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }

    // Security Filter Chain
    @Bean
    SecurityFilterChain filterChain(
            HttpSecurity http,
            JwtFilter jwt) throws Exception {

        return http

                // Disable CSRF Protection
                .csrf(c -> c.disable())

                // Enable CORS Configuration
                .cors(c -> c.configurationSource(cors()))

                // Configure Stateless Session
                .sessionManagement(s ->
                        s.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Configure API Authorization
                .authorizeHttpRequests(a ->
                        a.requestMatchers("/auth/**", "/error")
                                .permitAll()
                                .anyRequest()
                                .authenticated()
                )

                // Add JWT Filter Before Username Password Filter
                .addFilterBefore(
                        jwt,
                        UsernamePasswordAuthenticationFilter.class
                )

                .build();
    }

    // CORS Configuration
    @Bean
    CorsConfigurationSource cors() {

        CorsConfiguration c = new CorsConfiguration();

        // Allowed Frontend Origins
        c.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "http://127.0.0.1:5173"
        ));

        // Allowed HTTP Methods
        c.setAllowedMethods(List.of(
                "GET",
                "POST",
                "PUT",
                "DELETE",
                "OPTIONS"
        ));

        // Allowed Headers
        c.setAllowedHeaders(List.of("*"));

        // Allow Credentials
        c.setAllowCredentials(true);

        // Register CORS Configuration
        UrlBasedCorsConfigurationSource s =
                new UrlBasedCorsConfigurationSource();

        s.registerCorsConfiguration("/**", c);

        return s;
    }
}

// ==========================================
// 2. JWT FILTER
// ==========================================

@Component
class JwtFilter extends OncePerRequestFilter {

    // JWT Service Variable
    private final JwtService jwt;

    // Constructor Injection
    JwtFilter(JwtService jwt) {

        this.jwt = jwt;
    }

    // Filter Every Incoming HTTP Request
    @Override
    protected void doFilterInternal(
            HttpServletRequest req,
            HttpServletResponse res,
            FilterChain chain)
            throws ServletException, IOException {

        // Get Authorization Header
        String h = req.getHeader("Authorization");

        // Check Bearer Token
        if (h != null && h.startsWith("Bearer ")) {

            // Extract JWT Token
            String t = h.substring(7);

            // Validate JWT Token
            if (jwt.valid(t)) {

                // Extract Username From Token
                String u = jwt.username(t);

                // Create Authentication Object
                var a = new UsernamePasswordAuthenticationToken(
                        u,
                        null,
                        List.of()
                );

                // Set Authentication In Security Context
                SecurityContextHolder.getContext()
                        .setAuthentication(a);
            }
        }

        // Continue Filter Chain
        chain.doFilter(req, res);
    }
}
