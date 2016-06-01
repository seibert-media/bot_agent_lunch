package de.sjanusch.eventsystem.events.model;

import de.sjanusch.eventsystem.EventList;
import de.sjanusch.eventsystem.events.UserRoomEvent;
import de.sjanusch.model.hipchat.HipchatUser;
import de.sjanusch.model.hipchat.Room;

public class UserJoinedRoomEvent extends UserRoomEvent {

  private static final EventList events = new EventList();

  public UserJoinedRoomEvent(final Room room, final HipchatUser user, final String nick) {
    super(room, user, nick);
  }

  @Override
  public EventList getEvents() {
    return events;
  }

  public static EventList getEventList() {
    return events;
  }

}
