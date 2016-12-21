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
 * Created by Sandro Janusch Date: 19.05.16 Time: 10:25
 */
public class LunchLoginFlow implements LunchFlow {

  private static final Logger logger = LoggerFactory.getLogger(LunchLoginFlow.class);

  public static final String ANTWORT_FEHLER = "Bitte antworte mit Ja oder Nein!";

  private LunchMessageZustand actualZustand = null;

  private final TextHandler textHandler;

  private final PrivateMessageRecieverBase privateMessageRecieverBase;

  private final Weekdays weekday;

  private final SuperlunchRequestHandler superlunchRequestHandler;


  private Calendar cal;

  public LunchLoginFlow(final PrivateMessageRecieverBase privateMessageRecieverBase, final TextHandler textHandler,
                        final SuperlunchRequestHandler superlunchRequestHandler, final Weekdays weekday) {
    this.textHandler = textHandler;
    this.privateMessageRecieverBase = privateMessageRecieverBase;
    this.weekday = weekday;
    this.superlunchRequestHandler = superlunchRequestHandler;
    this.cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
  }

  @Override
  public LunchMessageZustand modifyFlowForUser(final String incomeMessage, final HipchatUser hipchatUser) {

    if (actualZustand == null) {
      this.actualZustand = LunchMessageZustand.ANMELDEN;
      privateMessageRecieverBase.sendPrivateMessageText(actualZustand.getText(), hipchatUser.getXmppUserId());
      return actualZustand;
    }

    if (actualZustand.equals(LunchMessageZustand.ANMELDEN)) {
      if (incomeMessage.contains("ja")) {
        actualZustand = LunchMessageZustand.ANMELDEN_JA;
        privateMessageRecieverBase.sendPrivateMessageText(actualZustand.getText(), hipchatUser.getXmppUserId());
      } else if (incomeMessage.contains("nein")) {
        actualZustand = LunchMessageZustand.ANMELDEN_NEIN;
        privateMessageRecieverBase.sendPrivateMessageText(actualZustand.getText(), hipchatUser.getXmppUserId());
      } else {
        privateMessageRecieverBase.sendPrivateMessageText(ANTWORT_FEHLER, hipchatUser.getXmppUserId());
      }
      return actualZustand;
    }

    if (actualZustand.equals(LunchMessageZustand.ANMELDEN_JA)) {
      final String id = this.extractId(incomeMessage);
      if (id != null && this.signIn(hipchatUser.getMention_name(), id)) {
        actualZustand = LunchMessageZustand.ANMELDUNG_ERFOLGREICH;
        privateMessageRecieverBase.sendPrivateNotificationSucess(actualZustand.getText() + " " + textHandler.getThankYouText(), hipchatUser.getXmppUserId());
        privateMessageRecieverBase.sendPrivateMessageText(textHandler.getRandomText(""), hipchatUser.getXmppUserId());
        privateMessageRecieverBase.sendPrivateMessageText("Du hast dich " + weekday.getText() + " zum Essen angemeldet", hipchatUser.getXmppUserId());
      } else {
        actualZustand = LunchMessageZustand.ANMELDUNG_FEHLGESCHLAGEN;
        privateMessageRecieverBase.sendPrivateNotificationError(actualZustand.getText(), hipchatUser.getXmppUserId());
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

  private boolean signIn(final String actualUser, final String id) {
    return superlunchRequestHandler.signInForLunch(id, actualUser);
  }

  private String extractId(final String incomeMessage) {
    final String[] strings = incomeMessage.split(" ");
    for (final String s : strings) {
      try {
        final int i = Integer.parseInt(s);
        return String.valueOf(i);
      } catch (final Exception e) {

      }
    }
    return null;
  }

  @Override
  public Calendar getCal() {
    return cal;
  }
}
