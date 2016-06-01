package de.sjanusch.eventsystem;
public abstract class Event {
    
    private String name;
    
    public Event() { }

    public abstract EventList getEvents();

    public String getEventName() {
        return ( name == null || name.equals( "" ) ) ? getClass().getSimpleName() : name;
    }
}

