package de.sjanusch.flow;

import de.sjanusch.confluence.handler.SuperlunchRequestHandler;
import de.sjanusch.listener.PrivateMessageRecieverBase;
import de.sjanusch.model.Weekdays;
import de.sjanusch.texte.TextHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

  private final String roomId;

  public LunchLogoutFlow(final PrivateMessageRecieverBase privateMessageRecieverBase, final TextHandler textHandler,
                         final SuperlunchRequestHandler superlunchRequestHandler, final int signedInNumber, final Weekdays weekday, final String roomId) {
    this.textHandler = textHandler;
    this.privateMessageRecieverBase = privateMessageRecieverBase;
    this.superlunchRequestHandler = superlunchRequestHandler;
    this.signedInNumber = signedInNumber;
    this.weekday = weekday;
    this.roomId = roomId;
  }

  @Override
  public LunchMessageZustand modifyFlowForUser(final String incomeMessage, final String user) {
    if (actualZustand == null) {
      this.actualZustand = LunchMessageZustand.ABMELDEN;
      privateMessageRecieverBase.sendPrivateMessageText(actualZustand.getText(), user);
      return actualZustand;
    }

    if (actualZustand.equals(LunchMessageZustand.ABMELDEN)) {
      if (incomeMessage.contains("ja")) {
        if (signedInNumber != 0 && this.signOut(user, String.valueOf(signedInNumber))) {
          actualZustand = LunchMessageZustand.ABMELDEN_ERFOLGREICH;
          privateMessageRecieverBase.sendPrivateNotificationSucess(actualZustand.getText() + " " + textHandler.getThankYouText(), user);
          privateMessageRecieverBase.sendPrivateMessageText(textHandler.getRandomText(""), user);
          if (this.roomId != null && this.roomId != "") {
            privateMessageRecieverBase.sendMessageTextToRoom(user + " hat sich " + weekday.getText() + " vom Essen abgemeldet", this.roomId);
          } else {
            privateMessageRecieverBase.sendPrivateMessageText("Du hast dich " + weekday.getText() + " vom Essen abgemeldet", user);
          }
        } else {
          actualZustand = LunchMessageZustand.ABMELDEN_FEHLGESCHLAGEN;
          privateMessageRecieverBase.sendPrivateNotificationError(actualZustand.getText(), user);
        }
      } else if (incomeMessage.contains("nein")) {
        actualZustand = LunchMessageZustand.ABMELDEN_NEIN;
        privateMessageRecieverBase.sendPrivateMessageText(actualZustand.getText(), user);
      } else {
        privateMessageRecieverBase.sendPrivateMessageText(ANTWORT_FEHLER, user);
      }
      return actualZustand;
    }
    return null;
  }

  private boolean signOut(final String actualUser, final String id) {
    return superlunchRequestHandler.signOutForLunch(id, actualUser);
  }
}
