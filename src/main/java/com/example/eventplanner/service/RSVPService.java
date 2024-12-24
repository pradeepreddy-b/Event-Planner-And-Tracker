package com.example.eventplanner.service;

import com.example.eventplanner.model.Event;
import com.example.eventplanner.model.RSVP;
import com.example.eventplanner.model.User;
import com.example.eventplanner.repository.EventRepository;
import com.example.eventplanner.repository.RSVPRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RSVPService {

    private final RSVPRepository rsvpRepository;
    private final EventRepository eventRepository;

    public RSVPService(RSVPRepository rsvpRepository, EventRepository eventRepository) {
        this.rsvpRepository = rsvpRepository;
        this.eventRepository = eventRepository;
    }

    public RSVP rsvpToEvent(Long eventId, User user, RSVP.Status status) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        if (event.getDate().isBefore(java.time.LocalDateTime.now())) {
            throw new RuntimeException("Cannot RSVP to past events");
        }

        RSVP rsvp = rsvpRepository.findByUserAndEvent(user, event).orElse(new RSVP());
        rsvp.setUser(user);
        rsvp.setEvent(event);
        rsvp.setStatus(status);

        return rsvpRepository.save(rsvp);
    }

    public RSVP getRSVPForUserAndEvent(User user, Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        return rsvpRepository.findByUserAndEvent(user, event)
                .orElseThrow(() -> new RuntimeException("RSVP not found for user and event"));
    }

    public List<RSVP> getAllRSVPsForEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        return rsvpRepository.findAllByEvent(event);
    }
    
    public List<RSVP> getAllRSVPsForUser(User user) {
        return rsvpRepository.findAllByUser(user);
    }

}
