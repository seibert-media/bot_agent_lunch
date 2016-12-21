package de.sjanusch.bot;

import com.google.inject.Inject;
import de.sjanusch.configuration.BotConfiguration;
import de.sjanusch.networking.ChatClient;
import de.sjanusch.networking.Connection;
import de.sjanusch.networking.exceptions.LoginException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class BotImpl implements Bot {

  private static final Logger logger = LoggerFactory.getLogger(BotImpl.class);

  private final Connection connection;

  private final BotConfiguration botConfiguration;

  private final ChatClient chatClient;

  private MultiUserChat chat;

  @Inject
  public BotImpl(final Connection connection, final BotConfiguration botConfiguration, final ChatClient chatClient) {
    this.connection = connection;
    this.botConfiguration = botConfiguration;
    this.chatClient = chatClient;
  }

  @Override
  public void run() {
    try {
      connection.connect();
      boolean loggedIn = false;
      if (connection.isConnected()) {
        loggedIn = chatClient.login(connection.getXmpp(), this.getUsername(), this.getPassword());
        if (loggedIn) {

        }
      }
    } catch (final XMPPException | LoginException | IOException e) {
      logger.warn(e.getClass().getName(), e);
      try {
        connection.disconnect();
      } catch (final XMPPException e2) {
        logger.debug("disconnect failed", e2);
      }
    }
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

  private List<String> getBotroom() throws IOException {
    return botConfiguration.getBotChatRoom();
  }
}
