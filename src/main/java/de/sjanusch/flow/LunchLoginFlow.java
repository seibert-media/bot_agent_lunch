package de.sjanusch.flow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.sjanusch.confluence.handler.SuperlunchRequestHandler;
import de.sjanusch.listener.MessageRecieverBase;
import de.sjanusch.texte.TextHandler;

/**
 * Created by Sandro Janusch
 * Date: 19.05.16
 * Time: 10:25
 */
public class LunchLoginFlow implements LunchFlow {

    private static final Logger logger = LoggerFactory.getLogger(LunchLoginFlow.class);

    public static final String ANTWORT_FEHLER = "Bitte antworte mit Ja oder Nein!";

    private LunchMessageZustand actualZustand = null;

  private final LunchFlowHelper lunchFlowHelper = new LunchFlowHelper();

    private final TextHandler textHandler;

    private final MessageRecieverBase messageRecieverBase;

    private final SuperlunchRequestHandler superlunchRequestHandler;

    public LunchLoginFlow(final MessageRecieverBase messageRecieverBase, final TextHandler textHandler, final SuperlunchRequestHandler superlunchRequestHandler) {
        this.textHandler = textHandler;
        this.messageRecieverBase = messageRecieverBase;
        this.superlunchRequestHandler = superlunchRequestHandler;
    }

  @Override
  public LunchMessageZustand modifyFlowForUser(final String incomeMessage, final String user) {

        if (actualZustand == null) {
            this.actualZustand = LunchMessageZustand.ANMELDEN;
            messageRecieverBase.sendMessageText(user, actualZustand.getText());
            return actualZustand;
        }

        if (actualZustand.equals(LunchMessageZustand.ANMELDEN)) {
            if (incomeMessage.contains("ja")) {
                actualZustand = LunchMessageZustand.ANMELDEN_JA;
                this.messageRecieverBase.sendMessageText(user, actualZustand.getText());
            } else if (incomeMessage.contains("nein")) {
                actualZustand = LunchMessageZustand.ANMELDEN_NEIN;
                this.messageRecieverBase.sendMessageText(user, actualZustand.getText());
            } else {
                this.messageRecieverBase.sendMessageText(user, ANTWORT_FEHLER);
            }
            return actualZustand;
        }

        if (actualZustand.equals(LunchMessageZustand.ANMELDEN_JA)) {
            final String id = lunchFlowHelper.extractId(incomeMessage);
            if (id != null && this.signIn(user, id)) {
                actualZustand = LunchMessageZustand.ANMELDUNG_ERFOLGREICH;
                messageRecieverBase.sendMessageHtmlSucess(user, actualZustand.getText() + " " + textHandler.getThankYouText());
                //this.messageRecieverBase.sendMessageText(user, textHandler.getRandomGeneratedText());
            } else {
                actualZustand = LunchMessageZustand.ANMELDUNG_FEHLGESCHLAGEN;
                messageRecieverBase.sendMessageHtmlError(user, actualZustand.getText());
            }
            return actualZustand;
        }
        return null;
    }

    private boolean signIn(final String actualUser, final String id) {
        return superlunchRequestHandler.signInForLunch(id, actualUser);
    }

}
