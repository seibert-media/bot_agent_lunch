package de.sjanusch.networking;

import com.google.inject.Inject;
import de.sjanusch.bot.Bot;
import de.sjanusch.configuration.ChatConnectionConfiguration;
import de.sjanusch.eventsystem.EventSystem;
import de.sjanusch.eventsystem.events.model.MessageRecivedEvent;
import de.sjanusch.model.hipchat.Room;
import de.sjanusch.networking.exceptions.LoginException;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by Sandro Janusch
 * Date: 02.06.16
 * Time: 14:07
 */

public class ChatClientImpl implements ChatClient {

    private static final Logger logger = LoggerFactory.getLogger(ChatClientImpl.class);

    private final ChatConnectionConfiguration chatConnectionConfiguration;

    private final EventSystem eventSystem;

    private final Bot bot;

    private MultiUserChat chat;

    @Inject
    public ChatClientImpl(final ChatConnectionConfiguration chatConnectionConfiguration, final EventSystem eventSystem, final Bot bot) {
        this.chatConnectionConfiguration = chatConnectionConfiguration;
        this.eventSystem = eventSystem;
        this.bot = bot;
    }

    public ChatConnectionConfiguration getChatConnectionConfiguration() {
        return chatConnectionConfiguration;
    }

    public void login(final XMPPConnection xmpp, String username, String password) throws LoginException {
        if (!username.contains("hipchat.com"))
            logger.error("The username being used does not look like a Jabber ID. Are you sure this is the correct username?");
        try {
            xmpp.login(username, password);
        } catch (XMPPException exception) {
            throw new LoginException("There was an error logging in! Are you using the correct username/password?", exception);
        }
    }

    public HashMap<Room, MultiUserChat> joinChat(final XMPPConnection xmpp, final String room, final String user, final String password) {
        if (user.equals("") || password.equals("")) {
            return null;
        }
        try {
            chat = new MultiUserChat(xmpp, this.getChatRoomName(room));
            chat.join(user, password);
            final Room obj = joinChatRoom(new Room(bot, eventSystem), room, xmpp);
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
            } else {
                logger.error("Cannot join in room " + room);
            }
        } catch (IOException e) {
            logger.error("Error while creating Chat!");
        } catch (XMPPException e) {
            e.printStackTrace();
        }
        return null;
    }

    public MultiUserChat getGroupChat() {
        return chat;
    }

    private Room joinChatRoom(final Room roomObject, final String roomName, XMPPConnection con) {
        try {
            roomObject.setName(roomName);
            roomObject.setChat(chat);
            roomObject.info = MultiUserChat.getRoomInfo(con, this.getChatRoomName(roomName));
            return roomObject;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XMPPException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getChatRoomName(final String room) throws IOException {
        return room.indexOf("@") != -1 ? room : room + "@" + chatConnectionConfiguration.getConfUrl();
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
                if (obj instanceof Message.Body)
                    return ((Message.Body) obj).getMessage();
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }

}
