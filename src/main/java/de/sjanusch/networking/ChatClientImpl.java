package de.sjanusch.networking;

import com.google.inject.Inject;
import de.sjanusch.bot.Bot;
import de.sjanusch.configuration.ChatConnectionConfiguration;
import de.sjanusch.eventsystem.EventSystem;
import de.sjanusch.eventsystem.events.model.MessageRecivedEvent;
import de.sjanusch.eventsystem.events.model.PrivateMessageRecivedEvent;
import de.sjanusch.model.hipchat.Room;
import de.sjanusch.networking.exceptions.LoginException;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.Occupant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Iterator;

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

  @Override
  public boolean login(final XMPPConnection xmpp, final String username, final String password) throws LoginException {
    if (!username.contains("hipchat.com")) {
      logger.error("The username being used does not look like a Jabber ID. Are you sure this is the correct username?");
      return false;
    }
    try {
      xmpp.login(username, password);
      return true;
    } catch (final XMPPException exception) {
      throw new LoginException("There was an error logging in! Are you using the correct username/password?", exception);
    }
  }

  @Override
  public boolean joinChat(final XMPPConnection xmpp, final String room, final String user, final String password) {
    if (user.equals("") || password.equals("")) {
      return false;
    }
    try {
      chat = new MultiUserChat(xmpp, this.getChatRoomName(room));
      chat.join(user, password);
      final Room obj = joinChatRoom(new Room(bot, eventSystem), room, xmpp);
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
        return true;
      } else {
        logger.error("Cannot join in room " + room);
      }
    } catch (final IOException e) {
      logger.error("Error while creating Chat!");
    } catch (final XMPPException e) {
      logger.warn(e.getClass().getName(), e);
    }
    return false;
  }

  @Override
  public void startPrivateChat(final String username) {
    final Iterator<String> occupantIterator = chat.getOccupants();
    while (occupantIterator.hasNext()) {
      final String occupantString = occupantIterator.next();
      if (occupantString.toLowerCase().contains(username.toLowerCase())) {
        final String userId = this.extractUserId(chat.getOccupant(occupantString));
        if (userId != null) {
          chat.createPrivateChat(userId, new MessageListener() {

            @Override
            public void processMessage(final Chat chat, final Message message) {
              final Message m = new Message();
              m.setBody(message.getBody());
              m.setFrom(username);
              final PrivateMessageRecivedEvent event = new PrivateMessageRecivedEvent(m);
              eventSystem.callEvent(event);
            }
          });
          logger.debug("Private Chat with " + userId + " created");
        }
      }
    }
  }

  private String extractUserId(final Occupant occupant) {
    final String[] values = occupant.getJid().split("/");
    if(values.length > 0){
      return values[0];
    }
    return null;
  }

  private Room joinChatRoom(final Room roomObject, final String roomName, final XMPPConnection con) {
    try {
      roomObject.setName(roomName);
      roomObject.setChat(chat);
      roomObject.info = MultiUserChat.getRoomInfo(con, this.getChatRoomName(roomName));
      return roomObject;
    } catch (IOException | XMPPException e) {
      logger.warn(e.getClass().getName(), e);
    }
    return null;
  }

  private String getChatRoomName(final String room) throws IOException {
    return room.contains("@") ? room : room + "@" + chatConnectionConfiguration.getConfUrl();
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
        if (obj instanceof Message.Body)
          return ((Message.Body) obj).getMessage();
      }
      return "";
    } catch (final Exception e) {
      return "";
    }
  }

}
