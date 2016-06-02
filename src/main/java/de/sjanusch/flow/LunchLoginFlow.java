package de.sjanusch.flow;

import de.sjanusch.confluence.handler.SuperlunchRequestHandler;
import de.sjanusch.listener.PrivateMessageRecieverBase;
import de.sjanusch.model.Weekdays;
import de.sjanusch.texte.TextHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Sandro Janusch Date: 19.05.16 Time: 10:25
 */
public class LunchLoginFlow implements LunchFlow {

  private static final Logger logger = LoggerFactory.getLogger(LunchLoginFlow.class);

  public static final String ANTWORT_FEHLER = "Bitte antworte mit Ja oder Nein!";

  private LunchMessageZustand actualZustand = null;

  private final LunchFlowHelper lunchFlowHelper = new LunchFlowHelper();

  private final TextHandler textHandler;

  private final PrivateMessageRecieverBase privateMessageRecieverBase;

  private final Weekdays weekday;

  private final SuperlunchRequestHandler superlunchRequestHandler;

  public LunchLoginFlow(final PrivateMessageRecieverBase privateMessageRecieverBase, final TextHandler textHandler,
                        final SuperlunchRequestHandler superlunchRequestHandler, final Weekdays weekday) {
    this.textHandler = textHandler;
    this.privateMessageRecieverBase = privateMessageRecieverBase;
    this.weekday = weekday;
    this.superlunchRequestHandler = superlunchRequestHandler;
  }

  @Override
  public LunchMessageZustand modifyFlowForUser(final String incomeMessage, final String user) {

    if (actualZustand == null) {
      this.actualZustand = LunchMessageZustand.ANMELDEN;
      privateMessageRecieverBase.sendMessageText(actualZustand.getText(), user);
      return actualZustand;
    }

    if (actualZustand.equals(LunchMessageZustand.ANMELDEN)) {
      if (incomeMessage.contains("ja")) {
        actualZustand = LunchMessageZustand.ANMELDEN_JA;
        privateMessageRecieverBase.sendMessageText(actualZustand.getText(), user);
      } else if (incomeMessage.contains("nein")) {
        actualZustand = LunchMessageZustand.ANMELDEN_NEIN;
        privateMessageRecieverBase.sendMessageText(actualZustand.getText(), user);
      } else {
        privateMessageRecieverBase.sendMessageText(ANTWORT_FEHLER, user);
      }
      return actualZustand;
    }

    if (actualZustand.equals(LunchMessageZustand.ANMELDEN_JA)) {
      final String id = lunchFlowHelper.extractId(incomeMessage);
      if (id != null && this.signIn(user, id)) {
        actualZustand = LunchMessageZustand.ANMELDUNG_ERFOLGREICH;
        privateMessageRecieverBase.sendNotificationSucess(actualZustand.getText() + " " + textHandler.getThankYouText(), user);
        privateMessageRecieverBase.sendMessageText(textHandler.getRandomText(""), user);
        privateMessageRecieverBase.sendMessageTextToRoom(user + " hat sich " + weekday.getText() + " zum Essen angemeldet");
      } else {
        actualZustand = LunchMessageZustand.ANMELDUNG_FEHLGESCHLAGEN;
        privateMessageRecieverBase.sendNotificationError(actualZustand.getText(), user);
      }
      return actualZustand;
    }
    return null;
  }

  private boolean signIn(final String actualUser, final String id) {
    return superlunchRequestHandler.signInForLunch(id, actualUser);
  }

}
