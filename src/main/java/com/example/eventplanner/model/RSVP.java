package com.example.eventplanner.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "rsvp")
public class RSVP {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnore // Prevent full User object from being serialized
    private User user;

    @ManyToOne
    @JsonIgnore // Prevent full Event object from being serialized
    private Event event;

    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status {
        ACCEPTED,
        DECLINED
    }

    // Custom getters for required fields

    @JsonProperty("userId")
    public Long getUserId() {
        return user.getId();
    }

    @JsonProperty("username")
    public String getUsername() {
        return user.getUsername();
    }

    @JsonProperty("email")
    public String getEmail() {
        return user.getEmail();
    }

    @JsonProperty("eventId")
    public Long getEventId() {
        return event.getId();
    }

    @JsonProperty("eventName")
    public String getEventName() {
        return event.getName();
    }
}
