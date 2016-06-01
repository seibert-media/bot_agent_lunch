package de.sjanusch.eventsystem.events.model;

import de.sjanusch.eventsystem.EventList;
import de.sjanusch.eventsystem.events.RoomEvent;
import de.sjanusch.model.hipchat.Room;
import org.jivesoftware.smack.packet.Message;

public class MessageRecivedEvent extends RoomEvent {

    
    private static final EventList events = new EventList();
    private Message message;
    
    public MessageRecivedEvent(Room room, Message message) {
        super(room);
        this.message = message;
    }

    public String from() {
        return message.getFrom();
    }

    public String body() {
        return message.getBody() == null ? "" : message.getBody();
    }

    public Message getMessage() {
        return message;
    }

    @Override
    public EventList getEvents() {
        return events;
    }
    
    public static EventList getEventList() {
        return events;
    }

}
