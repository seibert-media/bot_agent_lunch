package de.sjanusch.listener;

import com.google.inject.Inject;
import de.sjanusch.confluence.handler.SuperlunchRequestHandler;
import de.sjanusch.eventsystem.EventHandler;
import de.sjanusch.eventsystem.events.model.PrivateMessageRecivedEvent;
import de.sjanusch.flow.LunchFlow;
import de.sjanusch.flow.LunchLoginFlow;
import de.sjanusch.flow.LunchLogoutFlow;
import de.sjanusch.flow.LunchMessageZustand;
import de.sjanusch.model.Weekdays;
import de.sjanusch.model.superlunch.Lunch;
import de.sjanusch.protocol.LunchMessageProtocol;
import de.sjanusch.texte.TextHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Sandro Janusch
 * Date: 18.05.16
 * Time: 20:33
 */
public class LunchPrivateMessageRecieveListenerImpl implements LunchPrivateMessageRecieveListener {

  private static final Logger logger = LoggerFactory.getLogger(LunchPrivateMessageRecieveListenerImpl.class);

  private final LunchListenerHelper lunchListenerHelper;

  private final TextHandler textHandler;

  private final PrivateMessageRecieverBase privateMessageRecieverBase;

  private final LunchMessageProtocol lunchMessageProtocol;

  private final LinkedList<PrivateMessageRecivedEvent> privateMessageRecivedEvents;

  @Inject
  public LunchPrivateMessageRecieveListenerImpl(final LunchListenerHelper lunchListenerHelper, final TextHandler textHandler, final PrivateMessageRecieverBase privateMessageRecieverBase, final LunchMessageProtocol lunchMessageProtocol) {
    this.lunchListenerHelper = lunchListenerHelper;
    this.textHandler = textHandler;
    this.privateMessageRecieverBase = privateMessageRecieverBase;
    this.lunchMessageProtocol = lunchMessageProtocol;
    privateMessageRecivedEvents = new LinkedList<>();
    this.startPrivateMessageTimer();
  }

  @SuppressWarnings("unused")
  @EventHandler
  @Override
  public void messageEvent(final PrivateMessageRecivedEvent event) {
    privateMessageRecivedEvents.add(event);
  }

  private void handleMessage(final String message, final String from) throws ParseException, IOException {
    if (message == null) {
      logger.debug("No Message to Handle: " + message);
      return;
    }
    final String incomeMessage = message.toLowerCase().trim();
    final String actualUser = lunchListenerHelper.convertNames(from);
    final LunchFlow lunchFlow = lunchMessageProtocol.getCurrentFlowForUser(actualUser);

    logger.debug("Handle Message from " + actualUser + ": " + incomeMessage);

    if (lunchFlow == null && textHandler.containsLunchLoginText(incomeMessage) || textHandler.conatainsLunchLoginCommands(incomeMessage)) {
      this.handleMittagessenInfoMessage(incomeMessage, actualUser, true);
      return;
    }

    if (lunchFlow == null && textHandler.containsLunchLogoutText(incomeMessage) || textHandler.conatainsLunchLogoutCommands(incomeMessage)) {
      this.handleMittagessenInfoMessage(incomeMessage, actualUser, false);
      return;
    }

    if (lunchFlow != null && lunchFlow.getClass().equals(LunchLoginFlow.class)) {
      final LunchMessageZustand actualZustand = lunchFlow.modifyFlowForUser(incomeMessage, actualUser);
      if (actualZustand != null) {
        if (actualZustand.equals(LunchMessageZustand.ANMELDUNG_ERFOLGREICH)
          || actualZustand.equals(LunchMessageZustand.ANMELDEN_NEIN)
          || actualZustand.equals(LunchMessageZustand.ANMELDUNG_FEHLGESCHLAGEN)) {
          lunchMessageProtocol.removeFlowForUser(actualUser);
          lunchListenerHelper.setSignedInNumber(0);
          return;
        }
      }
      return;
    }

    if (lunchFlow != null && lunchFlow.getClass().equals(LunchLogoutFlow.class)) {
      final LunchMessageZustand actualZustand = lunchFlow.modifyFlowForUser(incomeMessage, actualUser);
      if (actualZustand != null) {
        if (actualZustand.equals(LunchMessageZustand.ABMELDEN_ERFOLGREICH)
          || actualZustand.equals(LunchMessageZustand.ABMELDEN_NEIN)
          || actualZustand.equals(LunchMessageZustand.ABMELDEN_FEHLGESCHLAGEN)) {
          lunchMessageProtocol.removeFlowForUser(actualUser);
          lunchListenerHelper.setSignedInNumber(0);
          return;
        }
      }
      return;
    }

    if (textHandler.containsHelpCommand(incomeMessage)) {
      privateMessageRecieverBase.sendNotification(textHandler.getHelpText(), actualUser);
      return;
    }
  }

  private void handleMittagessenInfoMessage(final String incomeMessage, final String actualUser, final boolean login) throws ParseException {
    final Weekdays weekday = Weekdays.getEnumForText(incomeMessage);
    if (weekday.isWeekend()) {
      final String text = "<b>Am " + weekday.getText() + " gibt es kein Mittagessen!</b>";
      privateMessageRecieverBase.sendNotificationError(text, actualUser);
    } else {
      final List<Lunch> lunchList = lunchListenerHelper.getLunchlist(weekday);
      if (lunchList.size() > 0) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<b>Mittagessen " + weekday.getText() + "</b><br>");
        lunchListenerHelper.setSignedInNumber(0);
        stringBuilder.append(lunchListenerHelper.createLunchOverview(lunchList, actualUser));
        privateMessageRecieverBase.sendNotification(stringBuilder.toString(), actualUser);
        final SuperlunchRequestHandler superlunchRequestHandler = lunchListenerHelper.getSuperlunchRequestHandler();
        if (!lunchListenerHelper.isLunchesClosed() && lunchListenerHelper.getSignedInNumber() == 0 && login) {
          final LunchFlow lunchLoginFlow = new LunchLoginFlow(privateMessageRecieverBase, textHandler, superlunchRequestHandler, weekday);
          lunchLoginFlow.modifyFlowForUser(incomeMessage, actualUser);
          lunchMessageProtocol.addFlowForUser(actualUser, lunchLoginFlow);
        }
        if (!lunchListenerHelper.isLunchesClosed() && (lunchListenerHelper.getSignedInNumber() != 0 || !login)) {
          final LunchFlow lunchLogoutFlow = new LunchLogoutFlow(privateMessageRecieverBase, textHandler, superlunchRequestHandler,
            lunchListenerHelper.getSignedInNumber(), weekday);
          lunchLogoutFlow.modifyFlowForUser(incomeMessage, actualUser);
          lunchMessageProtocol.addFlowForUser(actualUser, lunchLogoutFlow);
        }
      } else {
        privateMessageRecieverBase.sendNotificationError(textHandler.getOverviewErrorText(), actualUser);
      }
    }
  }

  private void startPrivateMessageTimer() {
    final Timer timer = new Timer("PrivateMessageTimer");
    final TimerTask timerTask = new TimerTask() {

      @Override
      public void run() {

        try {
          if (privateMessageRecivedEvents.size() > 0) {
            final PrivateMessageRecivedEvent privateMessageRecivedEvent = privateMessageRecivedEvents.getLast();
            if (privateMessageRecivedEvent != null) {
              logger.debug("Handle Privatemessage: " + privateMessageRecivedEvent.getMessage().getBody());
              handleMessage(privateMessageRecivedEvent.getMessage().getBody(), privateMessageRecivedEvent.getMessage().getFrom());
              privateMessageRecivedEvents.remove(privateMessageRecivedEvent);
            }
          }
        } catch (ParseException e) {
          logger.error("ParseException: " + e.getMessage());
        } catch (IOException e) {
          logger.error("IOException: " + e.getMessage());
        }
      }
    };
    timer.scheduleAtFixedRate(timerTask, 0, 1000);
    logger.debug("PrivateMessageTimer started");
  }

}
