package de.sjanusch.networking;

import com.github.brainlag.nsq.NSQProducer;
import com.github.brainlag.nsq.exceptions.NSQException;
import com.google.inject.Inject;
import de.sjanusch.configuration.ChatConnectionConfiguration;
import de.sjanusch.configuration.NSQConfiguration;
import de.sjanusch.model.hipchat.Room;
import de.sjanusch.model.nsq.NsqPrivateMessage;
import de.sjanusch.model.nsq.NsqPublicMessage;
import de.sjanusch.networking.exceptions.LoginException;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
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
import java.util.concurrent.TimeoutException;

/**
 * Created by Sandro Janusch
 * Date: 02.06.16
 * Time: 14:07
 */

public class ChatClientImpl implements ChatClient {

  private static final Logger logger = LoggerFactory.getLogger(ChatClientImpl.class);

  private final ChatConnectionConfiguration chatConnectionConfiguration;

  private final NSQConfiguration nsqConfiguration;

  @Inject
  public ChatClientImpl(final ChatConnectionConfiguration chatConnectionConfiguration, final NSQConfiguration nsqConfiguration) {
    this.chatConnectionConfiguration = chatConnectionConfiguration;
    this.nsqConfiguration = nsqConfiguration;
  }

  @Override
  public boolean login(final XMPPConnection xmpp, final String username, final String password) throws LoginException, IOException {
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
  public MultiUserChat joinChat(final XMPPConnection xmpp, final String room, final String user, final String password) {
    if (user.equals("") || password.equals("")) {
      return null;
    }
    try {
      final MultiUserChat chat = new MultiUserChat(xmpp, this.getChatRoomName(room));
      chat.join(user, password);
      final Room chatRoom = joinChatRoom(new Room(), room, xmpp, chat);
      if (chatRoom != null) {
        chat.addMessageListener(new PacketListener() {

          @Override
          public void processPacket(final Packet paramPacket) {
            final Message m = new Message();
            m.setBody(toMessage(paramPacket));
            m.setFrom(paramPacket.getFrom().split("\\/")[1]);
            try {
              NsqPublicMessage nsqPublicMessage = new NsqPublicMessage(m.getFrom(), m.getBody(), chatRoom.getXMPPName());
              if (nsqPublicMessage.getText() != null && nsqPublicMessage.getFullName() != null && nsqPublicMessage.getRoom() != null) {
                final byte[] serializedObject = serializeObject(nsqPublicMessage);
                if (serializedObject != null) {
                  final NSQProducer producer = new NSQProducer();
                  producer.addAddress(nsqConfiguration.getNSQAdress(), nsqConfiguration.getNSQAdressPort()).start();
                  producer.produce("PublicChat", serializedObject);
                }
              }
            } catch (NSQException e) {
              logger.error("NSQException " + e.getMessage());
            } catch (TimeoutException e) {
              logger.error("TimeoutException " + e.getMessage());
            } catch (IOException e) {
              logger.error("IOException " + e.getMessage());
            }
          }
        });
        return chat;
      } else {
        logger.error("Cannot join in room " + room);
      }
    } catch (final IOException e) {
      logger.error("Error while creating Chat!");
    } catch (final XMPPException e) {
      logger.warn(e.getClass().getName(), e);
    }
    return null;
  }

  @Override
  public void startPrivateChat(final String username, final MultiUserChat chat) {
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
              try {
                NsqPrivateMessage nsqPrivateMessage = new NsqPrivateMessage(username, m.getBody());
                if (nsqPrivateMessage.getText() != null && nsqPrivateMessage.getFullName() != null) {
                  final byte[] serializedObject = serializeObject(nsqPrivateMessage);
                  if (serializedObject != null) {
                    final NSQProducer producer = new NSQProducer();
                    producer.addAddress(nsqConfiguration.getNSQAdress(), nsqConfiguration.getNSQAdressPort()).start();
                    producer.produce("PrivateChat", serializedObject);
                  }
                }
              } catch (NSQException e) {
                logger.error("NSQException " + e.getMessage());
              } catch (TimeoutException e) {
                logger.error("TimeoutException " + e.getMessage());
              } catch (IOException e) {
                logger.error("IOException " + e.getMessage());
              }
            }
          });
          logger.debug("Private Chat with " + userId + " created");
        }
      }
    }
  }

  private String extractUserId(final Occupant occupant) {
    final String[] values = occupant.getJid().split("/");
    if (values.length > 0) {
      return values[0];
    }
    return null;
  }

  private Room joinChatRoom(final Room roomObject, final String roomName, final XMPPConnection con, final MultiUserChat chat) {
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

  private byte[] serializeObject(final Object object) {
    try {
      final ObjectMapper mapper = new ObjectMapper();
      final String json = mapper.writeValueAsString(object);
      return json.getBytes();
    } catch (JsonGenerationException e) {
      logger.error("JsonGenerationException " + e.getMessage());
    } catch (JsonMappingException e) {
      logger.error("JsonMappingException " + e.getMessage());
    } catch (IOException e) {
      logger.error("IOException " + e.getMessage());
    }
    return null;
  }

}
