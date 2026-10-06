package models;

public class Event {
    private EventType eventType;
    private int minute;
    private Player player;

    //CONSTRUCTOR

    public Event(EventType eventType, int minute, Player player) {
        this.eventType = eventType;
        this.minute = minute;
        this.player = player;
    }

    //GETTERS


    public EventType getEventName() {
        return eventType;
    }

    public int getMinute() {
        return minute;
    }

    public Player getPlayer() {
        return player;
    }
}