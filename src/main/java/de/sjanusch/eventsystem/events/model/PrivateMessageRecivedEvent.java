package de.sjanusch.eventsystem.events.model;

import de.sjanusch.eventsystem.Event;
import de.sjanusch.eventsystem.EventList;
import org.jivesoftware.smack.packet.Message;

public class PrivateMessageRecivedEvent extends Event {

  private static final EventList events = new EventList();

  private final Message message;

  public PrivateMessageRecivedEvent(final Message message) {
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
