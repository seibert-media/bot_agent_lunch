package de.sjanusch.networking;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

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

  private final XMPPConnection xmpp;

    private boolean connected;

    private String password;

  private final HashMap<Room, MultiUserChat> rooms = new HashMap<>();

  private final HashMap<String, Chat> cache = new HashMap<>();

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

  @Override
  public void connect() throws XMPPException {
        if (connected)
            return;
        xmpp.connect();
        xmpp.addConnectionListener(this);
        connected = true;
    }

  @Override
  public void login(final String username, final String password) throws LoginException {
        if (!connected)
            return;
        if (!username.contains("hipchat.com"))
            logger.error("The username being used does not look like a Jabber ID. Are you sure this is the correct username?");
        try {
            xmpp.login(username, password);
    } catch (final XMPPException exception) {
            throw new LoginException("There was an error logging in! Are you using the correct username/password?", exception);
        }
        this.password = password;
    }

  @Override
  public void joinRoom(final String room, final String nickname) throws XMPPException, IOException {
        if (!connected || nickname.equals("") || password.equals("")) {
            return;
        }
    chat = new MultiUserChat(xmpp, (room.contains("@") ? room : room + "@" + chatConnectionConfiguration.getConfUrl()));
        chat.join(nickname, password);
        final Room obj = joinChatRoom(room, chat, xmpp);
        if (obj != null) {
            chat.addMessageListener(new PacketListener() {

                @Override
        public void processPacket(final Packet paramPacket) {
          final Message m = new Message();
                    m.setBody(toMessage(paramPacket));
                    m.setFrom(paramPacket.getFrom().split("\\/")[1]);
          final MessageRecivedEvent event = new MessageRecivedEvent(obj, m);
                    eventSystem.callEvent(event);
                }
            });
            rooms.put(obj, chat);
        } else {
            logger.error("Cannot join in room " + room);
        }
    }

  private Room joinChatRoom(final String name, final MultiUserChat chat, final XMPPConnection con) {
        try {
            final Room r = new Room(this.bot, this.eventSystem);
            r.setName(name);
            r.setChat(chat);
      r.info = MultiUserChat.getRoomInfo(con, (name.contains("@") ? name : name + "@" + chatConnectionConfiguration.getConfUrl()));
            return r;
    } catch (final IOException | XMPPException e) {
            e.printStackTrace();
        }
    return null;
    }

  @Override
  public Room findRoom(final String name) {
        return this.findConnectedRoom(name);
    }

    private List<Room> getRooms() {
    final ArrayList<Room> roomlist = rooms.keySet().stream().collect(Collectors.toCollection(ArrayList::new));
    return Collections.unmodifiableList(roomlist);
    }

  private Room findConnectedRoom(final String name) {
    for (final Room r : getRooms()) {
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
  public void processMessage(final Chat arg0, final Message arg1) {
    final MessageRecivedEvent event = new MessageRecivedEvent(null, arg1);
        eventSystem.callEvent(event);
    }

    @Override
    public void connectionClosed() {
        connected = false;
    }

    @Override
  public void connectionClosedOnError(final Exception e) {
        connected = false;
    }

    @Override
  public void reconnectingIn(final int seconds) {

    }

    @Override
  public void reconnectionFailed(final Exception e) {
        if (connected)
            connected = false;
    }

    @Override
    public void reconnectionSuccessful() {
        if (!connected)
            connected = true;
    }

  @Override
  public synchronized void waitForEnd() throws InterruptedException {
        while (true) {
            if (!connected)
                break;
            super.wait(0L);
        }
    }
    
  private String toMessage(final Packet packet) {
        try {
      final Field f = packet.getClass().getDeclaredField("bodies");
            f.setAccessible(true);
      @SuppressWarnings("rawtypes")
      final HashSet h = (HashSet) f.get(packet);
            if (h.size() == 0)
                return "";
      for (final Object obj : h) {
                if (obj instanceof Body)
                    return ((Body) obj).getMessage();
            }
            return "";
    } catch (final Exception e) {
            return "";
        }
        
    }
}
