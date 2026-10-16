package com.ligafutbol.api.models;

import jakarta.persistence.*;

@Embeddable
public class Event {

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    private int minuteEvent;

    @ManyToOne
    private Player player;

    //CONSTRUCTORS
    public Event() {}

    public Event(EventType eventType, int minuteEvent, Player player) {
        this.eventType = eventType;
        this.minuteEvent = minuteEvent;
        this.player = player;
    }

    //GETTERS
    public EventType getEventName() {
        return eventType;
    }

    public int getMinute() {
        return minuteEvent;
    }

    public Player getPlayer() {
        return player;
    }
}