package de.sjanusch.eventsystem.events;

import de.sjanusch.eventsystem.Event;
import de.sjanusch.model.hipchat.Room;

public abstract class RoomEvent extends Event {
    
  private final Room room;
    
  public RoomEvent(final Room room) {
        this.room = room;
    }

    public Room getRoom() {
        return room;
    }

}
