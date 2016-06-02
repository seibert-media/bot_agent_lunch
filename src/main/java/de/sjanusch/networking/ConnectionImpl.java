package de.sjanusch.networking;

import com.google.inject.Inject;
import de.sjanusch.eventsystem.EventSystem;
import de.sjanusch.eventsystem.events.model.MessageRecivedEvent;
import de.sjanusch.model.hipchat.Room;
import de.sjanusch.networking.exceptions.LoginException;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public final class ConnectionImpl implements Connection, MessageListener, ConnectionListener {

    private static final Logger logger = LoggerFactory.getLogger(ConnectionImpl.class);

    private final ChatClient chatClient;

    private final EventSystem eventSystem;

    private XMPPConnection xmpp;

    private boolean connected;

    private String password;

    private HashMap<Room, MultiUserChat> rooms = new HashMap<Room, MultiUserChat>();

    @Inject
    public ConnectionImpl(final ChatClient chatClient, final EventSystem eventSystem) throws IOException {
        this.chatClient = chatClient;
        this.eventSystem = eventSystem;
        this.xmpp = new XMPPConnection(new ConnectionConfiguration(chatClient.getChatConnectionConfiguration().getXmppUrl(), chatClient.getChatConnectionConfiguration().getXmppPort()));
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

        rooms = chatClient.joinChat(xmpp, room, nickname, password);

            /*
            chat.createPrivateChat("23504_541247@chat.hipchat.com", new MessageListener() {

                @Override
                public void processMessage(final Chat chat, final Message message) {
                    Message m = new Message();
                    m.setBody(message.getBody());
                    m.setFrom(chat.getParticipant());
                    MessageRecivedEvent event = new MessageRecivedEvent(obj, m);
                    eventSystem.callEvent(event);
                }
            });
            */


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


}
