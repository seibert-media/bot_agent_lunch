package de.sjanusch.eventsystem.events;

import de.sjanusch.eventsystem.Event;
import de.sjanusch.model.hipchat.Room;

public abstract class RoomEvent extends Event {
    
    private Room room;
    
    public RoomEvent(Room room) {
        this.room = room;
    }

    public Room getRoom() {
        return room;
    }

}
