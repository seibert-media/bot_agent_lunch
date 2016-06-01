package de.sjanusch.flow;

import de.sjanusch.confluence.handler.SuperlunchRequestHandler;
import de.sjanusch.listener.MessageRecieverBase;
import de.sjanusch.texte.TextHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Sandro Janusch
 * Date: 23.05.16
 * Time: 20:07
 */
public class LunchLogoutFlow implements LunchFlow {

    private static final Logger logger = LoggerFactory.getLogger(LunchLogoutFlow.class);

    public static final String ANTWORT_FEHLER = "Bitte antworte mit Ja oder Nein!";

    private LunchMessageZustand actualZustand = null;

    private LunchFlowHelper lunchFlowHelper = new LunchFlowHelper();

    private final TextHandler textHandler;

    private final MessageRecieverBase messageRecieverBase;

    private final SuperlunchRequestHandler superlunchRequestHandler;

    private final int signedInNumber;

    public LunchLogoutFlow(final MessageRecieverBase messageRecieverBase, final TextHandler textHandler, final SuperlunchRequestHandler superlunchRequestHandler, final int signedInNumber) {
        this.textHandler = textHandler;
        this.messageRecieverBase = messageRecieverBase;
        this.superlunchRequestHandler = superlunchRequestHandler;
        this.signedInNumber = signedInNumber;
    }

    @Override
    public LunchMessageZustand modifyFlowForUser(final String incomeMessage, final String user) {
        if (actualZustand == null) {
            this.actualZustand = LunchMessageZustand.ABMELDEN;
            messageRecieverBase.sendMessageText(user, actualZustand.getText());
            return actualZustand;
        }

        if (actualZustand.equals(LunchMessageZustand.ABMELDEN)) {
            if (incomeMessage.contains("ja")) {
                if (signedInNumber != 0 && this.signOut(user, String.valueOf(signedInNumber))) {
                    actualZustand = LunchMessageZustand.ABMELDEN_ERFOLGREICH;
                    messageRecieverBase.sendMessageHtmlSucess(user, actualZustand.getText() + " " + textHandler.getThankYouText());
                } else {
                    actualZustand = LunchMessageZustand.ABMELDEN_FEHLGESCHLAGEN;
                    messageRecieverBase.sendMessageHtmlError(user, actualZustand.getText());
                }
            } else if (incomeMessage.contains("nein")) {
                actualZustand = LunchMessageZustand.ABMELDEN_NEIN;
                this.messageRecieverBase.sendMessageText(user, actualZustand.getText());
            } else {
                this.messageRecieverBase.sendMessageText(user, ANTWORT_FEHLER);
            }
            return actualZustand;
        }
        return null;
    }

    private boolean signOut(final String actualUser, final String id) {
        return superlunchRequestHandler.signOutForLunch(id, actualUser);
    }
}
