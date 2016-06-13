package de.sjanusch.listener;

import com.google.inject.Inject;
import de.sjanusch.bot.Bot;
import de.sjanusch.confluence.handler.SuperlunchRequestHandler;
import de.sjanusch.eventsystem.EventHandler;
import de.sjanusch.eventsystem.events.model.MessageRecivedEvent;
import de.sjanusch.flow.LunchFlow;
import de.sjanusch.flow.LunchLoginFlow;
import de.sjanusch.flow.LunchLogoutFlow;
import de.sjanusch.model.Weekdays;
import de.sjanusch.model.superlunch.Lunch;
import de.sjanusch.protocol.LunchMessageProtocol;
import de.sjanusch.texte.TextHandler;
import org.jivesoftware.smack.packet.Message;
import org.json.JSONException;
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

  @SuppressWarnings("unused")
  @EventHandler
  @Override
  public void messageEvent(final MessageRecivedEvent event) {
    try {
      final String from = event.from();
      if (!privateMessageRecieverBase.isMessageFromBot(from)) {
        handleMessage(event.getMessage(), from);
      }
    } catch (JSONException e) {
      logger.error(e.getMessage());
    } catch (ParseException e) {
      logger.error(e.getMessage());
    } catch (IOException e) {
      logger.error(e.getMessage());
    }
  }

  private void handleMessage(final Message message, final String from) throws ParseException, IOException, JSONException {
    final String incomeMessage = message.getBody().toLowerCase().trim();
    final String actualUser = lunchListenerHelper.convertNames(from);
    final LunchFlow lunchFlow = lunchMessageProtocol.getCurrentFlowForUser(actualUser);

    logger.debug("Handle Message from " + actualUser + ": " + incomeMessage);

    if (lunchFlow == null && textHandler.containsLunchLoginText(incomeMessage) || textHandler.conatainsLunchLoginCommands(incomeMessage)) {
      this.handleMittagessenInfoMessage(incomeMessage, actualUser, from, true);
      return;
    }

    if (lunchFlow == null && textHandler.containsLunchLogoutText(incomeMessage) || textHandler.conatainsLunchLogoutCommands(incomeMessage)) {
      this.handleMittagessenInfoMessage(incomeMessage, actualUser, from, false);
      return;
    }

    if (lunchFlow != null && lunchFlow.getClass().equals(LunchLoginFlow.class)) {
      lunchMessageProtocol.removeFlowForUser(actualUser);
      return;
    }

    if (lunchFlow != null && lunchFlow.getClass().equals(LunchLogoutFlow.class)) {
      lunchMessageProtocol.removeFlowForUser(actualUser);
      lunchListenerHelper.setSignedInNumber(0);
      return;
    }

    if (textHandler.containsHelpCommand(incomeMessage)) {
      privateMessageRecieverBase.sendNotification(textHandler.getHelpText(), actualUser);
      return;
    }
  }

  private void handleMittagessenInfoMessage(final String incomeMessage, final String actualUser, final String fullName, final boolean login) throws JSONException, ParseException {
    Weekdays weekday = Weekdays.getEnumForText(incomeMessage);
    if (weekday.isWeekend()) {
      final String text = "<b>Am " + weekday.getText() + " gibt es kein Mittagessen!</b>";
      privateMessageRecieverBase.sendNotificationError(text, actualUser);
    } else {
      List<Lunch> lunchList = lunchListenerHelper.getLunchlist(weekday);
      if (lunchList.size() > 0) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<b>Mittagessen " + weekday.getText() + "</b><br>");
        stringBuilder.append(lunchListenerHelper.createLunchOverview(lunchList, actualUser));
        privateMessageRecieverBase.sendNotification(stringBuilder.toString(), actualUser);
        SuperlunchRequestHandler superlunchRequestHandler = lunchListenerHelper.getSuperlunchRequestHandler();
        if (!lunchListenerHelper.isLunchesClosed() && lunchListenerHelper.getSignedInNumber() == 0 && login) {
          LunchFlow lunchLoginFlow = new LunchLoginFlow(privateMessageRecieverBase, textHandler, superlunchRequestHandler, weekday);
          lunchLoginFlow.modifyFlowForUser(incomeMessage, actualUser);
          lunchMessageProtocol.addFlowForUser(actualUser, lunchLoginFlow);
        }
        if (!lunchListenerHelper.isLunchesClosed() && (lunchListenerHelper.getSignedInNumber() != 0 || !login)) {
          LunchFlow lunchLogoutFlow = new LunchLogoutFlow(privateMessageRecieverBase, textHandler, superlunchRequestHandler, lunchListenerHelper.getSignedInNumber(), weekday);
          lunchLogoutFlow.modifyFlowForUser(incomeMessage, actualUser);
          lunchMessageProtocol.addFlowForUser(actualUser, lunchLogoutFlow);
        }
        bot.startPrivateChat(fullName);
      } else {
        privateMessageRecieverBase.sendNotificationError(textHandler.getOverviewErrorText(), actualUser);
      }
    }
  }

}
