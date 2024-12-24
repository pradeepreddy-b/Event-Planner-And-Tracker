package com.example.eventplanner.repository;

import com.example.eventplanner.model.Event;
import com.example.eventplanner.model.RSVP;
import com.example.eventplanner.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RSVPRepository extends JpaRepository<RSVP, Long> {
    Optional<RSVP> findByUserAndEvent(User user, Event event);
    List<RSVP> findAllByUser(User user);
    List<RSVP> findAllByEvent(Event event);  // Fetch all RSVPs for a specific event
}
