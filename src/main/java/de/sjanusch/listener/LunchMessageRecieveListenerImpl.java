package de.sjanusch.listener;

import com.google.inject.Inject;
import de.sjanusch.protocol.LunchMessageProtocol;
import de.sjanusch.texte.TextHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.ParseException;

/**
 * Created by Sandro Janusch
 * Date: 18.05.16
 * Time: 20:33
 */
public class LunchMessageRecieveListenerImpl implements LunchMessageRecieveListener {

  private static final Logger logger = LoggerFactory.getLogger(LunchMessageRecieveListenerImpl.class);

  private final LunchListenerHelper lunchListenerHelper;

  private final TextHandler textHandler;

  private final PrivateMessageRecieverBase privateMessageRecieverBase;

  @Inject
  public LunchMessageRecieveListenerImpl(final LunchListenerHelper lunchListenerHelper, final TextHandler textHandler, final LunchMessageProtocol lunchMessageProtocol, final PrivateMessageRecieverBase privateMessageRecieverBase) {
    this.lunchListenerHelper = lunchListenerHelper;
    this.textHandler = textHandler;
    this.privateMessageRecieverBase = privateMessageRecieverBase;
  }

  public boolean handleMessage(final String message, final String from, final String roomId) throws ParseException, IOException {
    final String incomeMessage = message.toLowerCase().trim();
    final String actualUser = lunchListenerHelper.convertNames(from);

    if (privateMessageRecieverBase.isMessageFromBot(actualUser)) {
      return true;
    }

    logger.debug("Handle Public Message from " + actualUser + ": " + incomeMessage);

    if (textHandler.containsLunchLoginText(incomeMessage) || textHandler.conatainsLunchLoginCommands(incomeMessage)) {
      this.handleMittagessenInfoMessage(roomId);
      return true;
    }

    if (textHandler.containsLunchLogoutText(incomeMessage) || textHandler.conatainsLunchLogoutCommands(incomeMessage)) {
      this.handleMittagessenInfoMessage(roomId);
      return true;
    }

    if (textHandler.containsHelpCommand(incomeMessage)) {
      privateMessageRecieverBase.sendPrivateNotification(textHandler.getHelpText(), actualUser);
      return true;
    }
    return true;
  }

  private void handleMittagessenInfoMessage(final String roomId) throws ParseException {
    privateMessageRecieverBase.sendMessageTextToRoom("Halllo ", roomId);
  }
}
