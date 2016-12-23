package de.sjanusch.networking;

import com.github.brainlag.nsq.NSQProducer;
import com.github.brainlag.nsq.exceptions.NSQException;
import com.google.inject.Inject;
import de.sjanusch.configuration.ChatConnectionConfiguration;
import de.sjanusch.configuration.NSQConfiguration;
import de.sjanusch.model.hipchat.HipchatUser;
import de.sjanusch.model.nsq.NsqPrivateMessage;
import de.sjanusch.networking.exceptions.LoginException;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketFilter;
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
      final MultiUserChat chat = new MultiUserChat(xmpp, room + "@" + chatConnectionConfiguration.getConfUrl());
      chat.join(user, password);
      return chat;
    } catch (XMPPException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  private void startPersonalCommunication(final String text, final HipchatUser hipchatUser) {
    try {
      logger.debug("Private Chat with " + hipchatUser.getMention_name() + " created");
      NsqPrivateMessage nsqPrivateMessage = new NsqPrivateMessage(text, hipchatUser);
      if (nsqPrivateMessage.getText() != null && nsqPrivateMessage.getHipchatUser() != null) {
        final byte[] serializedObject = chatClientHelper.serializeObject(nsqPrivateMessage);
        if (serializedObject != null) {
          final NSQProducer producer = new NSQProducer();
          producer.addAddress(nsqConfiguration.getNSQAdress(), nsqConfiguration.getNSQAdressPort()).start();
          producer.produce(nsqConfiguration.getNsqTopicName(), serializedObject);
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

  private final class ChatPacketListener implements PacketListener {

    @Override
    public void processPacket(final Packet packet) {
      final String message = chatClientHelper.toMessage(packet);
      final String a = packet.toXML();
      final String userId = chatClientHelper.extractHipchatUserId(packet.getFrom().split("\\/")[0]);
      if (!chatClientHelper.isPacketFromRoom(packet)) {
        final HipchatUser hipchatUser = chatClientHelper.chatUserExists(userId);
        if (hipchatUser != null) {
          hipchatUser.setXmppUserId(userId);
          startPersonalCommunication(message, hipchatUser);
        }
      }
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
