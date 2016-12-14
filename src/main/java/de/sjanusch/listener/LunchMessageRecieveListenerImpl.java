package de.sjanusch.listener;

import com.google.inject.Inject;
import de.sjanusch.bot.Bot;
import de.sjanusch.confluence.handler.SuperlunchRequestHandler;
import de.sjanusch.flow.LunchFlow;
import de.sjanusch.flow.LunchLoginFlow;
import de.sjanusch.flow.LunchLogoutFlow;
import de.sjanusch.model.Weekdays;
import de.sjanusch.model.superlunch.Lunch;
import de.sjanusch.protocol.LunchMessageProtocol;
import de.sjanusch.texte.TextHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

/**
 * Created by Sandro Janusch
 * Date: 18.05.16
 * Time: 20:33
 */
public class LunchMessageRecieveListenerImpl implements LunchMessageRecieveListener {

  private static final Logger logger = LoggerFactory.getLogger(LunchMessageRecieveListenerImpl.class);

  private final LunchListenerHelper lunchListenerHelper;

  private final TextHandler textHandler;

  private final LunchMessageProtocol lunchMessageProtocol;

  private final Bot bot;

  private final PrivateMessageRecieverBase privateMessageRecieverBase;

  @Inject
  public LunchMessageRecieveListenerImpl(final LunchListenerHelper lunchListenerHelper, final TextHandler textHandler, final LunchMessageProtocol lunchMessageProtocol, final Bot bot, final PrivateMessageRecieverBase privateMessageRecieverBase) {
    this.lunchListenerHelper = lunchListenerHelper;
    this.textHandler = textHandler;
    this.lunchMessageProtocol = lunchMessageProtocol;
    this.bot = bot;
    this.privateMessageRecieverBase = privateMessageRecieverBase;
  }

  public boolean handleMessage(final String message, final String from, final String roomId) throws ParseException, IOException {
    final String incomeMessage = message.toLowerCase().trim();
    final String actualUser = lunchListenerHelper.convertNames(from);
    final LunchFlow lunchFlow = lunchMessageProtocol.getCurrentFlowForUser(actualUser);

    logger.debug("Handle Message from " + actualUser + ": " + incomeMessage);

    if (lunchFlow == null && textHandler.containsLunchLoginText(incomeMessage) || textHandler.conatainsLunchLoginCommands(incomeMessage)) {
      this.handleMittagessenInfoMessage(incomeMessage, actualUser, from, true, roomId);
      return true;
    }

    if (lunchFlow == null && textHandler.containsLunchLogoutText(incomeMessage) || textHandler.conatainsLunchLogoutCommands(incomeMessage)) {
      this.handleMittagessenInfoMessage(incomeMessage, actualUser, from, false, roomId);
      return true;
    }

    if (lunchFlow != null && lunchFlow.getClass().equals(LunchLoginFlow.class)) {
      lunchMessageProtocol.removeFlowForUser(actualUser);
      lunchListenerHelper.setSignedInNumber(0);
      return true;
    }

    if (lunchFlow != null && lunchFlow.getClass().equals(LunchLogoutFlow.class)) {
      lunchMessageProtocol.removeFlowForUser(actualUser);
      lunchListenerHelper.setSignedInNumber(0);
      return true;
    }

    if (textHandler.containsHelpCommand(incomeMessage)) {
      privateMessageRecieverBase.sendPrivateNotification(textHandler.getHelpText(), actualUser);
      return true;
    }
    return false;
  }

  private void handleMittagessenInfoMessage(final String incomeMessage, final String actualUser, final String fullName, final boolean login, final String roomId) throws ParseException {
    final Weekdays weekday = Weekdays.getEnumForText(incomeMessage);
    if (weekday.isWeekend()) {
      final String text = "<b>Am " + weekday.getText() + " gibt es kein Mittagessen!</b>";
      privateMessageRecieverBase.sendPrivateNotificationError(text, actualUser);
    } else {
      final List<Lunch> lunchList = lunchListenerHelper.getLunchlist(weekday);
      if (lunchList.size() > 0) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<b>Mittagessen " + weekday.getText() + "</b><br>");
        lunchListenerHelper.setSignedInNumber(0);
        stringBuilder.append(lunchListenerHelper.createLunchOverview(lunchList, actualUser));
        privateMessageRecieverBase.sendPrivateNotification(stringBuilder.toString(), actualUser);
        final SuperlunchRequestHandler superlunchRequestHandler = lunchListenerHelper.getSuperlunchRequestHandler();
        if (!lunchListenerHelper.isLunchesClosed() && lunchListenerHelper.getSignedInNumber() == 0 && login) {
          final LunchFlow lunchLoginFlow = new LunchLoginFlow(privateMessageRecieverBase, textHandler, superlunchRequestHandler, weekday, roomId);
          lunchLoginFlow.modifyFlowForUser(incomeMessage, actualUser);
          lunchMessageProtocol.addFlowForUser(actualUser, lunchLoginFlow);
        }
        if (!lunchListenerHelper.isLunchesClosed() && (lunchListenerHelper.getSignedInNumber() != 0 || !login)) {
          final LunchFlow lunchLogoutFlow = new LunchLogoutFlow(privateMessageRecieverBase, textHandler, superlunchRequestHandler, lunchListenerHelper.getSignedInNumber(), weekday, roomId);
          lunchLogoutFlow.modifyFlowForUser(incomeMessage, actualUser);
          lunchMessageProtocol.addFlowForUser(actualUser, lunchLogoutFlow);
        }
        bot.startPrivateChat(fullName);
      } else {
        privateMessageRecieverBase.sendPrivateNotificationError(textHandler.getOverviewErrorText(), actualUser);
      }
    }
  }
}
