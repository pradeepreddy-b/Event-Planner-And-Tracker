package com.example.eventplanner.controller;

import com.example.eventplanner.model.RSVP;
import com.example.eventplanner.model.User;
import com.example.eventplanner.repository.UserRepository;
import com.example.eventplanner.service.RSVPService;
import com.example.eventplanner.exception.GlobalExceptionHandler.EventNotFoundException;
import com.example.eventplanner.exception.GlobalExceptionHandler.UserNotFoundException;
import com.example.eventplanner.exception.GlobalExceptionHandler.RSVPNotFoundException;
import org.springframework.security.core.Authentication;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rsvps")
public class RSVPController {

    private final RSVPService rsvpService;
    private final UserRepository userRepository;

    public RSVPController(RSVPService rsvpService, UserRepository userRepository) {
        this.rsvpService = rsvpService;
        this.userRepository = userRepository;
    }

    @PostMapping("/rsvpsevent/{eventId}")
    public RSVP rsvpToEvent(@PathVariable Long eventId, @RequestParam RSVP.Status status, Authentication authentication) {
        String username = authentication.getName();  // Get the logged-in user's email or username
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UserNotFoundException(username));  // Retrieve the user based on email/username

        // Perform RSVP operation
        try {
            return rsvpService.rsvpToEvent(eventId, user, status);  // Pass User object here
        } catch (EventNotFoundException e) {
            throw e;
        }
    }

    @GetMapping("/event/{eventId}/user/{userId}")
    public RSVP getRSVPForUserAndEvent(@PathVariable Long eventId, @PathVariable Long userId, Authentication authentication) {
        String username = authentication.getName();  // Get the logged-in user (the authenticated user making the request)
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        if (authentication.getAuthorities().stream().anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"))) {
            return rsvpService.getRSVPForUserAndEvent(user, eventId);
        } else {
            if (!user.getId().equals(userId)) {
                throw new AccessDeniedException("You do not have permission to view RSVP for other users");
            }
            return rsvpService.getRSVPForUserAndEvent(user, eventId);
        }
    }
    
    
 // Admin can view all RSVPs for a specific event
    @GetMapping("/event/{eventId}/registrations")
    public List<RSVP> getAllRSVPsForEvent(@PathVariable Long eventId, Authentication authentication) {
        if (!authentication.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            throw new AccessDeniedException("Only admins can view RSVPs for events");
        }
        return rsvpService.getAllRSVPsForEvent(eventId);
    }

    // Users can view their RSVP status for an event
    @GetMapping("/event/{eventId}")
    public RSVP getUserRSVPForEvent(@PathVariable Long eventId, Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UserNotFoundException(username));
        return rsvpService.getRSVPForUserAndEvent(user, eventId);
    }
    
    
    
 // Fetch all events a user has registered for
    @GetMapping("/user/events")
    public List<RSVP> getAllEventsForUser(Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        return rsvpService.getAllRSVPsForUser(user);
    }

}
