package com.example.eventplanner.config;

import com.example.eventplanner.model.CustomUserDetails;
import com.example.eventplanner.repository.UserRepository;
import com.example.eventplanner.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.GrantedAuthority;  // For GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority;  // For SimpleGrantedAuthority
import java.util.Collection;  // For Collection
import java.util.List;  // For List

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);  // Extract the token part after 'Bearer '
            String email = jwtUtils.extractEmail(token);  // Extract email from the token
            String role = jwtUtils.extractRole(token);  // Extract role from the token

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                var userOptional = userRepository.findByEmail(email);

                if (userOptional.isPresent() && jwtUtils.validateToken(token)) {
                    UserDetails userDetails = new CustomUserDetails(userOptional.get());

                    // Create authorities based on the extracted role
                 //   Collection<? extends GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
                    Collection<? extends GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role.startsWith("ROLE_") ? role : "ROLE_" + role));

                    // Create authentication token with authorities based on role
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, authorities);

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Set the authentication object in the SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }

        chain.doFilter(request, response);
    }
}
