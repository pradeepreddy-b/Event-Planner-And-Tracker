package com.example.eventplanner.controller;

import com.example.eventplanner.model.Event;
import com.example.eventplanner.model.enums.EventCategory;
import com.example.eventplanner.model.enums.Role;
import com.example.eventplanner.service.AuthService;
import com.example.eventplanner.service.EventService;
import com.example.eventplanner.exception.GlobalExceptionHandler.EventNotFoundException;
import com.example.eventplanner.exception.GlobalExceptionHandler.UserNotFoundException;
import com.example.eventplanner.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;
    
    private final AuthService authService;
    
    
    @Autowired
    public EventController(EventService eventService, AuthService authService) {
        this.eventService = eventService;
        this.authService = authService;
    }

    @PostMapping("/postevent")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
  
    public ResponseEntity<Event> createEvent(@RequestBody Event event) {
        // Get the current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        
        try {
            User user = authService.getUserByEmail(email); // Fetch the user from the database using email
            event.setCreatedBy(user); // Set the user creating the event
            Event createdEvent = eventService.createEvent(event); // Save the event in the database
            return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    
    
    @GetMapping("/getevents")
    public List<Event> getAllEvents() {
        return eventService.getAllEvents();
    }

    @GetMapping("/getevent/category/{category}")
    public List<Event> getEventsByCategory(@PathVariable EventCategory category) {
        return eventService.getEventsByCategory(category);
    }

    @PutMapping("/putevent/{eventId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Event updateEvent(@PathVariable Long eventId, @RequestBody Event eventDetails) {
        try {
            return eventService.updateEvent(eventId, eventDetails);
        } catch (EventNotFoundException e) {
            throw e;
        }
    }

    @DeleteMapping("/deleteevent/{eventId}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEvent(@PathVariable Long eventId) {
        try {
            eventService.deleteEvent(eventId);
        } catch (EventNotFoundException e) {
            throw e;
        }
    }
    
   

}

