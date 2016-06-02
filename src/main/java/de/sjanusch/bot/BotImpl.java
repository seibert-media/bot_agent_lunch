package de.sjanusch.bot;

import com.google.inject.Inject;
import de.sjanusch.configuration.BotConfiguration;
import de.sjanusch.eventsystem.EventSystem;
import de.sjanusch.listener.LuncheMessageRecieveListener;
import de.sjanusch.listener.MessageRecieveListener;
import de.sjanusch.model.hipchat.Room;
import de.sjanusch.networking.ChatClient;
import de.sjanusch.networking.Connection;
import de.sjanusch.networking.exceptions.LoginException;
import org.jivesoftware.smack.XMPPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class BotImpl implements Bot {

  private static final Logger logger = LoggerFactory.getLogger(BotImpl.class);

  private Room selected;

  private final EventSystem eventSystem;

  private final Connection connection;

  private final MessageRecieveListener messageRecieveListener;

  private final LuncheMessageRecieveListener luncheMessageRecieveListener;

  private final BotConfiguration botConfiguration;

  private final ChatClient chatClient;

  @Inject
  public BotImpl(final EventSystem eventSystem, final Connection connection, final MessageRecieveListener messageRecieveListener,
                 final LuncheMessageRecieveListener luncheMessageRecieveListener, final BotConfiguration botConfiguration, final ChatClient chatClient) {
    this.eventSystem = eventSystem;
    this.connection = connection;
    this.messageRecieveListener = messageRecieveListener;
    this.luncheMessageRecieveListener = luncheMessageRecieveListener;
    this.botConfiguration = botConfiguration;
    this.chatClient = chatClient;
  }

  @Override
  public void run() {
    // this.eventSystem.registerEvents(messageRecieveListener);
    this.eventSystem.registerEvents(luncheMessageRecieveListener);
    try {
      connection.connect();
      if(connection.isConnected()) {
        chatClient.login(connection.getXmpp(), this.getUsername(), this.getPassword());
        chatClient.joinChat(connection.getXmpp(), this.getBotroom(), this.getNickname(), this.getPassword());
        logger.debug("Joined " + this.getBotroom() + " !");
      }
    } catch (final XMPPException e) {
      logger.error("Error during join Room");
      logger.error(e.getMessage());
    } catch (LoginException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public Room getSelectedRoom() {
    return selected;
  }

  @Override
  public String getBotroom() throws IOException {
    return botConfiguration.getBotChatRoom();
  }

  @Override
  public String getNickname() throws IOException {
    return botConfiguration.getBotNickname();
  }

  @Override
  public String getPassword() throws IOException {
    return botConfiguration.getBotPassword();
  }

  @Override
  public String getUsername() throws IOException {
    return botConfiguration.getBotUsername();
  }
}
