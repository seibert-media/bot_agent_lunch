package de.sjanusch.model.hipchat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.RoomInfo;

import com.google.inject.Inject;

import de.sjanusch.bot.Bot;
import de.sjanusch.eventsystem.EventSystem;
import de.sjanusch.eventsystem.events.model.UserJoinedRoomEvent;
import de.sjanusch.eventsystem.events.model.UserLeftRoomEvent;

public class Room {

    public static final String CONF_URL = "conf.hipchat.com";
    
    private MultiUserChat chat;

    public RoomInfo info;

    public String subject;

    private String name;

    public HipchatRoomInfo hinfo;

  public final ArrayList<String> users = new ArrayList<>();

    private int lastcount;

    public String api_cache;

    private Thread joinchecker;

    private boolean halt;

    private final Bot bot;

    private final EventSystem eventSystem;

    @Inject
  public Room(final Bot bot, final EventSystem eventSystem) {
        this.bot = bot;
        this.eventSystem = eventSystem;
    }

    public void setChat(final MultiUserChat chat) {
        this.chat = chat;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void startThread() {
        joinchecker = new JoinLookout();
        joinchecker.start();
    }
    
    private void stopThread() {
        halt = true;
        joinchecker.interrupt();
        try {
            joinchecker.join();
    } catch (final InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public void disconnect() {
        stopThread();
        //TODO Disconnect
    }

    public int getUserCount() {
        if (!isConnected())
            return -1;
        return chat.getOccupantsCount();
    }

    public boolean isConnected() {
        return chat != null;
    }

    public String getXMPPName() {
    return (name.contains("@") ? name.split("\\@")[0] : name);
    }

    public String getXMPP_JID() {
    return (name.contains("@") ? name : name + "@" + CONF_URL);
    }

  public String getTrueName(final String APIKey) {
        if (hinfo == null) {
            hinfo = HipchatRoomInfo.getInfo(APIKey, this);
            if (hinfo == null)
                return null;
        }
        return hinfo.getRoomName();
    }

    public String getTrueName() {
        if (hinfo == null)
            return null;
        return hinfo.getRoomName();
    }

    public HipchatRoomInfo getHipchatRoomInfo() {
        return hinfo;
    }

  public HipchatRoomInfo getHipchatRoomInfo(final String APIKey) {
        if (hinfo == null) {
            hinfo = HipchatRoomInfo.getInfo(APIKey, this);
            if (hinfo == null)
                return null;
        }
        return hinfo;
    }

    public String getSubject() {
        if (subject == null || subject.equals("")) {
            if (info != null)
                subject = info.getSubject();
            else if (hinfo != null)
                subject = hinfo.getTopic();
        }
        return subject;
    }

  public boolean setSubject(final String subject) {
        if (chat == null)
            return false;
        try {
            chat.changeSubject(subject);
    } catch (final XMPPException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    public List<String> getConnectedUsers() {
    final Iterator<String> temp = chat.getOccupants();
    final List<String> copy = new ArrayList<>();
        while (temp.hasNext())
            copy.add(temp.next());
        return Collections.unmodifiableList(copy);
    }

  public boolean sendMessage(final String message, final String from) {
        if (chat == null)
            return false;
        try {
            chat.sendMessage(message);
            return true;
    } catch (final XMPPException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    private class JoinLookout extends Thread {
        
        @Override
        public void run() {
      final ArrayList<String> toremove = new ArrayList<>();
            while (isConnected()) {
                toremove.clear();
                if (halt)
                    continue;
                if (getUserCount() != lastcount) {
          final List<String> connected = getConnectedUsers();
          for (final String nick : connected) {
                        if (!users.contains(nick)) { //connected
                            HipchatUser user = null;
                            if (api_cache != null && !api_cache.equals(""))
                                user = HipchatUser.createInstance(nick.split("\\/")[1], api_cache);
                            users.add(nick);
                            lastcount = getUserCount();
              final UserJoinedRoomEvent event = new UserJoinedRoomEvent(Room.this, user, nick);
                            eventSystem.callEvent(event);
                        }
                    }

          for (final String nick : users) {
                        if (!connected.contains(nick)) { //disconnected
                            HipchatUser user = null;
                            if (api_cache != null && !api_cache.equals(""))
                                user = HipchatUser.createInstance(nick.split("\\/")[1], api_cache);
                            toremove.add(nick);
                            lastcount = getUserCount();
              final UserLeftRoomEvent event = new UserLeftRoomEvent(Room.this, user, nick);
                            eventSystem.callEvent(event);
                        }
                    }

          toremove.forEach(users::remove);
                }

                try {
                    Thread.sleep(500);
        } catch (final InterruptedException e) {
                    e.printStackTrace();
                }
                
            }
        }
    }

}
