package de.sjanusch.eventsystem.events;

import de.sjanusch.model.hipchat.HipchatUser;
import de.sjanusch.model.hipchat.Room;

public abstract class UserRoomEvent extends RoomEvent {

  private final HipchatUser user;

  private final String nick;
    
  public UserRoomEvent(final Room room, final HipchatUser user, final String nick) {
        super(room);
        this.nick = nick;
        this.user = user;
    }

    public HipchatUser getHipchatUser() {
        return user;
    }

    public String getNickname() {
        return nick.split("\\/")[1];
    }
    
    public String getJID() {
        return nick;
    }

}
