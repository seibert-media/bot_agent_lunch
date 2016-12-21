package de.sjanusch.listener;

import com.google.inject.Inject;
import de.sjanusch.confluence.handler.SuperlunchRequestHandler;
import de.sjanusch.flow.LunchFlow;
import de.sjanusch.flow.LunchLoginFlow;
import de.sjanusch.flow.LunchLogoutFlow;
import de.sjanusch.flow.LunchMessageZustand;
import de.sjanusch.model.Weekdays;
import de.sjanusch.model.hipchat.HipchatUser;
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
public class LunchPrivateMessageRecieveListenerImpl implements LunchPrivateMessageRecieveListener {

  private static final Logger logger = LoggerFactory.getLogger(LunchPrivateMessageRecieveListenerImpl.class);

  private final LunchListenerHelper lunchListenerHelper;

  private final TextHandler textHandler;

  private final PrivateMessageRecieverBase privateMessageRecieverBase;

  private final LunchMessageProtocol lunchMessageProtocol;

  @Inject
  public LunchPrivateMessageRecieveListenerImpl(final LunchListenerHelper lunchListenerHelper, final TextHandler textHandler, final PrivateMessageRecieverBase privateMessageRecieverBase, final LunchMessageProtocol lunchMessageProtocol) {
    this.lunchListenerHelper = lunchListenerHelper;
    this.textHandler = textHandler;
    this.privateMessageRecieverBase = privateMessageRecieverBase;
    this.lunchMessageProtocol = lunchMessageProtocol;
  }

  public boolean handleMessage(final String message, final HipchatUser hipchatUser) throws ParseException, IOException {
    final String incomeMessage = message.toLowerCase().trim();
    final LunchFlow lunchFlow = lunchMessageProtocol.getCurrentFlowForUser(hipchatUser.getXmppUserId());

    if (privateMessageRecieverBase.isMessageFromBot(hipchatUser.getMention_name())) {
      return true;
    }

    logger.debug("Handle Private Message from " + hipchatUser.getMention_name() + ": " + incomeMessage);

    if (lunchFlow == null && textHandler.containsLunchLoginText(incomeMessage) || textHandler.conatainsLunchLoginCommands(incomeMessage)) {
      this.handleMittagessenInfoMessage(incomeMessage, hipchatUser, true);
      return true;
    }

    if (lunchFlow == null && textHandler.containsLunchLogoutText(incomeMessage) || textHandler.conatainsLunchLogoutCommands(incomeMessage)) {
      this.handleMittagessenInfoMessage(incomeMessage, hipchatUser, false);
      return true;
    }

    if (lunchFlow != null && lunchFlow.getClass().equals(LunchLoginFlow.class)) {
      final LunchMessageZustand actualZustand = lunchFlow.modifyFlowForUser(incomeMessage, hipchatUser);
      if (actualZustand != null) {
        if (actualZustand.equals(LunchMessageZustand.ANMELDUNG_ERFOLGREICH)
          || actualZustand.equals(LunchMessageZustand.ANMELDEN_NEIN)
          || actualZustand.equals(LunchMessageZustand.ANMELDUNG_FEHLGESCHLAGEN)) {
          lunchMessageProtocol.removeFlowForUser(hipchatUser.getXmppUserId());
          lunchListenerHelper.setSignedInNumber(0);
          return true;
        }
      }
      return true;
    }

    if (lunchFlow != null && lunchFlow.getClass().equals(LunchLogoutFlow.class)) {
      final LunchMessageZustand actualZustand = lunchFlow.modifyFlowForUser(incomeMessage, hipchatUser);
      if (actualZustand != null) {
        if (actualZustand.equals(LunchMessageZustand.ABMELDEN_ERFOLGREICH)
          || actualZustand.equals(LunchMessageZustand.ABMELDEN_NEIN)
          || actualZustand.equals(LunchMessageZustand.ABMELDEN_FEHLGESCHLAGEN)) {
          lunchMessageProtocol.removeFlowForUser(hipchatUser.getXmppUserId());
          lunchListenerHelper.setSignedInNumber(0);
          return true;
        }
      }
      return true;
    }

    if (textHandler.containsHelpCommand(incomeMessage)) {
      privateMessageRecieverBase.sendPrivateNotification(textHandler.getHelpText(), hipchatUser.getXmppUserId());
      return true;
    }

    return true;
  }

  private void handleMittagessenInfoMessage(final String incomeMessage, final HipchatUser hipchatUser, final boolean login) throws ParseException {
    final Weekdays weekday = Weekdays.getEnumForText(incomeMessage);
    if (weekday.isWeekend()) {
      final String text = "<b>Am " + weekday.getText() + " gibt es kein Mittagessen!</b>";
      privateMessageRecieverBase.sendPrivateNotificationError(text, hipchatUser.getXmppUserId());
    } else {
      final List<Lunch> lunchList = lunchListenerHelper.getLunchlist(weekday);
      if (lunchList.size() > 0) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<b>Mittagessen " + weekday.getText() + "</b><br>");
        lunchListenerHelper.setSignedInNumber(0);
        stringBuilder.append(lunchListenerHelper.createLunchOverview(lunchList, hipchatUser.getXmppUserId()));
        privateMessageRecieverBase.sendPrivateNotification(stringBuilder.toString(), hipchatUser.getXmppUserId());
        final SuperlunchRequestHandler superlunchRequestHandler = lunchListenerHelper.getSuperlunchRequestHandler();
        if (!lunchListenerHelper.isLunchesClosed() && lunchListenerHelper.getSignedInNumber() == 0 && login) {
          final LunchFlow lunchLoginFlow = new LunchLoginFlow(privateMessageRecieverBase, textHandler, superlunchRequestHandler, weekday);
          lunchLoginFlow.modifyFlowForUser(incomeMessage, hipchatUser);
          lunchMessageProtocol.addFlowForUser(hipchatUser.getXmppUserId(), lunchLoginFlow);
        }
        if (!lunchListenerHelper.isLunchesClosed() && (lunchListenerHelper.getSignedInNumber() != 0 || !login)) {
          final LunchFlow lunchLogoutFlow = new LunchLogoutFlow(privateMessageRecieverBase, textHandler, superlunchRequestHandler,
            lunchListenerHelper.getSignedInNumber(), weekday);
          lunchLogoutFlow.modifyFlowForUser(incomeMessage, hipchatUser);
          lunchMessageProtocol.addFlowForUser(hipchatUser.getXmppUserId(), lunchLogoutFlow);
        }
      } else {
        privateMessageRecieverBase.sendPrivateNotificationError(textHandler.getOverviewErrorText(), hipchatUser.getXmppUserId());
      }
    }
  }
}
