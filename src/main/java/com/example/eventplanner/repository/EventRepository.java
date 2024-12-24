package com.example.eventplanner.repository;

import com.example.eventplanner.model.Event;
import com.example.eventplanner.model.User;
import com.example.eventplanner.model.enums.EventCategory;  // Import EventCategory enum
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByCreatedBy(User user);

    // Add this method to find events by category
    List<Event> findByCategory(EventCategory category);  // Define the method
}
