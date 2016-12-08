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

  private final TextHandler textHandler;

  private final PrivateMessageRecieverBase privateMessageRecieverBase;

  private final Weekdays weekday;

  private final SuperlunchRequestHandler superlunchRequestHandler;

  private final String roomId;

  public LunchLoginFlow(final PrivateMessageRecieverBase privateMessageRecieverBase, final TextHandler textHandler,
                        final SuperlunchRequestHandler superlunchRequestHandler, final Weekdays weekday, final String roomId) {
    this.textHandler = textHandler;
    this.privateMessageRecieverBase = privateMessageRecieverBase;
    this.weekday = weekday;
    this.superlunchRequestHandler = superlunchRequestHandler;
    this.roomId = roomId;
  }

  @Override
  public LunchMessageZustand modifyFlowForUser(final String incomeMessage, final String user) {

    if (actualZustand == null) {
      this.actualZustand = LunchMessageZustand.ANMELDEN;
      privateMessageRecieverBase.sendPrivateMessageText(actualZustand.getText(), user);
      return actualZustand;
    }

    if (actualZustand.equals(LunchMessageZustand.ANMELDEN)) {
      if (incomeMessage.contains("ja")) {
        actualZustand = LunchMessageZustand.ANMELDEN_JA;
        privateMessageRecieverBase.sendPrivateMessageText(actualZustand.getText(), user);
      } else if (incomeMessage.contains("nein")) {
        actualZustand = LunchMessageZustand.ANMELDEN_NEIN;
        privateMessageRecieverBase.sendPrivateMessageText(actualZustand.getText(), user);
      } else {
        privateMessageRecieverBase.sendPrivateMessageText(ANTWORT_FEHLER, user);
      }
      return actualZustand;
    }

    if (actualZustand.equals(LunchMessageZustand.ANMELDEN_JA)) {
      final String id = this.extractId(incomeMessage);
      if (id != null && this.signIn(user, id)) {
        actualZustand = LunchMessageZustand.ANMELDUNG_ERFOLGREICH;
        privateMessageRecieverBase.sendPrivateNotificationSucess(actualZustand.getText() + " " + textHandler.getThankYouText(), user);
        privateMessageRecieverBase.sendPrivateMessageText(textHandler.getRandomText(""), user);
        if (this.roomId != null && this.roomId != "") {
          privateMessageRecieverBase.sendMessageTextToRoom(user + " hat sich " + weekday.getText() + " zum Essen angemeldet", this.roomId);
        } else {
          privateMessageRecieverBase.sendPrivateMessageText("Du hast dich " + weekday.getText() + " zum Essen angemeldet", user);
        }
      } else {
        actualZustand = LunchMessageZustand.ANMELDUNG_FEHLGESCHLAGEN;
        privateMessageRecieverBase.sendPrivateNotificationError(actualZustand.getText(), user);
      }
      return actualZustand;
    }
    return null;
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

}
