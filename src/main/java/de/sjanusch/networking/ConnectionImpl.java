package de.sjanusch.networking;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Message.Body;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import de.sjanusch.bot.Bot;
import de.sjanusch.configuration.ChatConnectionConfiguration;
import de.sjanusch.eventsystem.EventSystem;
import de.sjanusch.eventsystem.events.model.MessageRecivedEvent;
import de.sjanusch.model.hipchat.Room;
import de.sjanusch.networking.exceptions.LoginException;

public final class ConnectionImpl implements Connection, MessageListener, ConnectionListener {

    private final ChatConnectionConfiguration chatConnectionConfiguration;

    private XMPPConnection xmpp;

    private boolean connected;

    private String password;

    private HashMap<Room, MultiUserChat> rooms = new HashMap<Room, MultiUserChat>();

    private HashMap<String, Chat> cache = new HashMap<String, Chat>();

    private static final Logger logger = LoggerFactory.getLogger(ConnectionImpl.class);

    private final EventSystem eventSystem;

    private final Bot bot;

    private MultiUserChat chat;

    @Inject
    public ConnectionImpl(final ChatConnectionConfiguration chatConnectionConfiguration, final EventSystem eventSystem, final Bot bot) throws IOException {
        this.chatConnectionConfiguration = chatConnectionConfiguration;
        this.eventSystem = eventSystem;
        this.bot = bot;
        this.xmpp = new XMPPConnection(new ConnectionConfiguration(chatConnectionConfiguration.getXmppUrl(), chatConnectionConfiguration.getXmppPort()));
    }

    public void connect() throws XMPPException {
        if (connected)
            return;
        xmpp.connect();
        xmpp.addConnectionListener(this);
        connected = true;
    }

    public void login(String username, String password) throws LoginException {
        if (!connected)
            return;
        if (!username.contains("hipchat.com"))
            logger.error("The username being used does not look like a Jabber ID. Are you sure this is the correct username?");
        try {
            xmpp.login(username, password);
        } catch (XMPPException exception) {
            throw new LoginException("There was an error logging in! Are you using the correct username/password?", exception);
        }
        this.password = password;
    }

    public void joinRoom(String room, String nickname) throws XMPPException, IOException {
        if (!connected || nickname.equals("") || password.equals("")) {
            return;
        }
        chat = new MultiUserChat(xmpp, (room.indexOf("@") != -1 ? room : room + "@" + chatConnectionConfiguration.getConfUrl()));
        chat.join(nickname, password);
        final Room obj = joinChatRoom(room, chat, xmpp);
        if (obj != null) {
            chat.addMessageListener(new PacketListener() {

                @Override
                public void processPacket(Packet paramPacket) {
                    Message m = new Message();
                    m.setBody(toMessage(paramPacket));
                    m.setFrom(paramPacket.getFrom().split("\\/")[1]);
                    MessageRecivedEvent event = new MessageRecivedEvent(obj, m);
                    eventSystem.callEvent(event);
                }
            });
            rooms.put(obj, chat);
        } else {
            logger.error("Cannot join in room " + room);
        }
    }

    private Room joinChatRoom(String name, MultiUserChat chat, XMPPConnection con) {
        try {
            final Room r = new Room(this.bot, this.eventSystem);
            r.setName(name);
            r.setChat(chat);
            r.info = MultiUserChat.getRoomInfo(con, (name.indexOf("@") != -1 ? name : name + "@" + chatConnectionConfiguration.getConfUrl()));
            return r;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XMPPException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Room findRoom(final String name) {
        return this.findConnectedRoom(name);
    }

    private List<Room> getRooms() {
        ArrayList<Room> roomlist = new ArrayList<Room>();
        for (Room room : rooms.keySet()) {
            roomlist.add(room);
        }
        return Collections.unmodifiableList(roomlist);
    }

    private Room findConnectedRoom(String name) {
        for (Room r : getRooms()) {
            if (r.getXMPPName().equals(name))
                return r;
        }
        return null;
    }

    public boolean isConnected() {
        return connected;
    }

    public void disconnect() {
        if (!connected)
            return;
        xmpp.disconnect();
        connected = false;
    }

    @Override
    public void processMessage(Chat arg0, Message arg1) {
        MessageRecivedEvent event = new MessageRecivedEvent(null, arg1);
        eventSystem.callEvent(event);
    }

    @Override
    public void connectionClosed() {
        connected = false;
    }

    @Override
    public void connectionClosedOnError(Exception e) {
        connected = false;
    }

    @Override
    public void reconnectingIn(int seconds) {

    }

    @Override
    public void reconnectionFailed(Exception e) {
        if (connected)
            connected = false;
    }

    @Override
    public void reconnectionSuccessful() {
        if (!connected)
            connected = true;
    }

    public synchronized void waitForEnd() throws InterruptedException {
        while (true) {
            if (!connected)
                break;
            super.wait(0L);
        }
    }
    
    private String toMessage(Packet packet) {
        try {
            Field f = packet.getClass().getDeclaredField("bodies");
            f.setAccessible(true);
            @SuppressWarnings("rawtypes")
            HashSet h = (HashSet) f.get(packet);
            if (h.size() == 0)
                return "";
            for (Object obj : h) {
                if (obj instanceof Body)
                    return ((Body) obj).getMessage();
            }
            return "";
        } catch (Exception e) {
            return "";
        }
        
    }
}
