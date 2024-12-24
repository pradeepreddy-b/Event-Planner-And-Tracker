package com.example.eventplanner.config;

import com.example.eventplanner.config.JwtFilter;  // Make sure JwtFilter is correctly imported
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
          
        .authorizeHttpRequests()
        .requestMatchers("/api/auth/**").permitAll()  // Public endpoints
     
        
        // Allow USERS and ADMINS to GET events
        .requestMatchers(HttpMethod.GET, "/api/events/getevents").hasAnyRole("USER", "ADMIN")
        .requestMatchers(HttpMethod.GET, "/api/events/getevent/category/**").hasAnyRole("USER", "ADMIN")
        
        // Allow only ADMINS to POST, PUT, DELETE events
        .requestMatchers(HttpMethod.POST, "/api/events/postevent").hasRole("ADMIN")
        .requestMatchers(HttpMethod.PUT, "/api/events/putevent/**").hasRole("ADMIN")
        .requestMatchers(HttpMethod.DELETE, "/api/events/deleteevent/**").hasRole("ADMIN")
        
     // Restrict RSVP POST endpoint to only USERS
        .requestMatchers(HttpMethod.POST, "/rsvps/rsvpsevent/**").hasRole("USER")

        // Allow USERS and ADMINS to GET RSVP details
        .requestMatchers(HttpMethod.GET, "/rsvps/event/**").hasAnyRole("USER", "ADMIN")

        
        // Default fallback: all other requests require authentication
        .anyRequest().authenticated()
       
        
        .and()
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
