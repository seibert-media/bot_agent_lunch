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
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
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

  private final ChatClientHelper chatClientHelper;

  @Inject
  public ChatClientImpl(final ChatConnectionConfiguration chatConnectionConfiguration, final NSQConfiguration nsqConfiguration, final ChatClientHelper chatClientHelper) {
    this.chatConnectionConfiguration = chatConnectionConfiguration;
    this.nsqConfiguration = nsqConfiguration;
    this.chatClientHelper = chatClientHelper;
  }

  @Override
  public boolean login(final XMPPConnection xmpp, final String username, final String password) throws LoginException, IOException {
    if (!username.contains("hipchat.com")) {
      logger.error("The username being used does not look like a Jabber ID. Are you sure this is the correct username?");
      return false;
    }
    try {
      xmpp.login(username, password);
      xmpp.addPacketListener(new ChatPacketListener(), new ChatPacketFilter(username));
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
            m.setBody(chatClientHelper.toMessage(paramPacket));
            m.setFrom(paramPacket.getFrom().split("\\/")[1]);
            try {
              NsqPublicMessage nsqPublicMessage = new NsqPublicMessage(m.getFrom(), m.getBody(), chatRoom.getXMPPName());
              if (nsqPublicMessage.getText() != null && nsqPublicMessage.getFullName() != null && nsqPublicMessage.getRoom() != null) {
                final byte[] serializedObject = chatClientHelper.serializeObject(nsqPublicMessage);
                if (serializedObject != null) {
                  final NSQProducer producer = new NSQProducer();
                  producer.addAddress(nsqConfiguration.getNSQAdress(), nsqConfiguration.getNSQAdressPort()).start();
                  producer.produce(nsqConfiguration.getNsqPublicTopicName(), serializedObject);
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

  /*
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

            }
          });
          logger.debug("Private Chat with " + userId + " created");
        }
      }
    }
  }
  */

  private void startPrivateCommunication(final String userId, final String text) {
    try {
      logger.debug("Private Chat with " + userId + " created");
      NsqPrivateMessage nsqPrivateMessage = new NsqPrivateMessage(userId, text);
      if (nsqPrivateMessage.getText() != null && nsqPrivateMessage.getFullName() != null) {
        final byte[] serializedObject = chatClientHelper.serializeObject(nsqPrivateMessage);
        if (serializedObject != null) {
          final NSQProducer producer = new NSQProducer();
          producer.addAddress(nsqConfiguration.getNSQAdress(), nsqConfiguration.getNSQAdressPort()).start();
          producer.produce(nsqConfiguration.getNsqPrivateTopicName(), serializedObject);
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

  private final class ChatPacketListener implements PacketListener {

    @Override
    public void processPacket(final Packet packet) {
      final String message = chatClientHelper.toMessage(packet);
      final String userId = chatClientHelper.extractHipchatUserId(packet.getFrom().split("\\/")[0]);
      startPrivateCommunication(userId, message);
    }

  }

  private final class ChatPacketFilter implements PacketFilter {

    private final String username;

    public ChatPacketFilter(final String username) {
      this.username = username;
    }

    @Override
    public boolean accept(final Packet packet) {
      if (chatClientHelper.isPacketForBot(packet, username)) {
        return true;
      }
      return false;
    }

  }

}
