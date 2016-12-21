package de.sjanusch.flow;

import de.sjanusch.confluence.handler.SuperlunchRequestHandler;
import de.sjanusch.listener.PrivateMessageRecieverBase;
import de.sjanusch.model.Weekdays;
import de.sjanusch.model.hipchat.HipchatUser;
import de.sjanusch.texte.TextHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by Sandro Janusch Date: 23.05.16 Time: 20:07
 */
public class LunchLogoutFlow implements LunchFlow {

  private static final Logger logger = LoggerFactory.getLogger(LunchLogoutFlow.class);

  public static final String ANTWORT_FEHLER = "Bitte antworte mit Ja oder Nein!";

  private LunchMessageZustand actualZustand = null;

  private final TextHandler textHandler;

  private final PrivateMessageRecieverBase privateMessageRecieverBase;

  private final SuperlunchRequestHandler superlunchRequestHandler;

  private final int signedInNumber;

  private final Weekdays weekday;

  private Calendar cal;

  public LunchLogoutFlow(final PrivateMessageRecieverBase privateMessageRecieverBase, final TextHandler textHandler,
                         final SuperlunchRequestHandler superlunchRequestHandler, final int signedInNumber, final Weekdays weekday) {
    this.textHandler = textHandler;
    this.privateMessageRecieverBase = privateMessageRecieverBase;
    this.superlunchRequestHandler = superlunchRequestHandler;
    this.signedInNumber = signedInNumber;
    this.weekday = weekday;
    this.cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
  }

  @Override
  public LunchMessageZustand modifyFlowForUser(final String incomeMessage, final HipchatUser hipchatUser) {
    if (actualZustand == null) {
      this.actualZustand = LunchMessageZustand.ABMELDEN;
      privateMessageRecieverBase.sendPrivateMessageText(actualZustand.getText(), hipchatUser.getXmppUserId());
      return actualZustand;
    }

    if (actualZustand.equals(LunchMessageZustand.ABMELDEN)) {
      if (incomeMessage.contains("ja")) {
        if (signedInNumber != 0 && this.signOut(hipchatUser.getMention_name(), String.valueOf(signedInNumber))) {
          actualZustand = LunchMessageZustand.ABMELDEN_ERFOLGREICH;
          privateMessageRecieverBase.sendPrivateNotificationSucess(actualZustand.getText() + " " + textHandler.getThankYouText(), hipchatUser.getXmppUserId());
          privateMessageRecieverBase.sendPrivateMessageText(textHandler.getRandomText(""), hipchatUser.getXmppUserId());
          privateMessageRecieverBase.sendPrivateMessageText("Du hast dich " + weekday.getText() + " vom Essen abgemeldet", hipchatUser.getXmppUserId());
        } else {
          actualZustand = LunchMessageZustand.ABMELDEN_FEHLGESCHLAGEN;
          privateMessageRecieverBase.sendPrivateNotificationError(actualZustand.getText(), hipchatUser.getXmppUserId());
        }
      } else if (incomeMessage.contains("nein")) {
        actualZustand = LunchMessageZustand.ABMELDEN_NEIN;
        privateMessageRecieverBase.sendPrivateMessageText(actualZustand.getText(), hipchatUser.getXmppUserId());
      } else {
        privateMessageRecieverBase.sendPrivateMessageText(ANTWORT_FEHLER, hipchatUser.getXmppUserId());
      }
      return actualZustand;
    }
    return null;
  }

  @Override
  public void flowReminder(final String user) {
    privateMessageRecieverBase.sendPrivateMessageText("Hallo, hast du mich vergessen?", user);
    privateMessageRecieverBase.sendPrivateMessageText(actualZustand.getText(), user);
  }

  @Override
  public Calendar getCal() {
    return cal;
  }

  private boolean signOut(final String actualUser, final String id) {
    return superlunchRequestHandler.signOutForLunch(id, actualUser);
  }
}
