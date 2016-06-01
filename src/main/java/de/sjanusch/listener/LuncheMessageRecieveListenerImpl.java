package de.sjanusch.listener;

import com.google.inject.Inject;
import de.sjanusch.configuration.BotConfiguration;
import de.sjanusch.confluence.handler.SuperlunchRequestHandler;
import de.sjanusch.eventsystem.EventHandler;
import de.sjanusch.eventsystem.events.model.MessageRecivedEvent;
import de.sjanusch.flow.LunchFlow;
import de.sjanusch.flow.LunchLoginFlow;
import de.sjanusch.flow.LunchLogoutFlow;
import de.sjanusch.flow.LunchMessageZustand;
import de.sjanusch.model.Weekdays;
import de.sjanusch.model.superlunch.Lunch;
import de.sjanusch.model.superlunch.Participant;
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
public class LuncheMessageRecieveListenerImpl implements LuncheMessageRecieveListener {

    private static final Logger logger = LoggerFactory.getLogger(LuncheMessageRecieveListenerImpl.class);

    private final SuperlunchRequestHandler superlunchRequestHandler;

    private final TextHandler textHandler;

    private final MessageRecieverBase messageRecieverBase;

    private final LunchMessageProtocol lunchMessageProtocol;

    private int lunchesClosed = 0;

    private boolean isLunchesClosed = false;

    private int signedInNumber = 0;

    @Inject
    public LuncheMessageRecieveListenerImpl(final SuperlunchRequestHandler superlunchRequestHandler, final TextHandler textHandler, final BotConfiguration botConfiguration, final MessageRecieverBase messageRecieverBase, final LunchMessageProtocol lunchMessageProtocol) {
        this.superlunchRequestHandler = superlunchRequestHandler;
        this.textHandler = textHandler;
        this.messageRecieverBase = messageRecieverBase;
        this.lunchMessageProtocol = lunchMessageProtocol;
    }

    @SuppressWarnings("unused")
    @EventHandler
    @Override
    public void messageEvent(final MessageRecivedEvent event) {
        try {
            final String from = event.from();
            if (!messageRecieverBase.isMessageFromBot(from)) {
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
        final String actualUser = messageRecieverBase.convertNames(from);
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
            LunchMessageZustand actualZustand = lunchFlow.modifyFlowForUser(incomeMessage, actualUser);
            if (actualZustand != null) {
                if (actualZustand.equals(LunchMessageZustand.ANMELDUNG_ERFOLGREICH)
                    || actualZustand.equals(LunchMessageZustand.ANMELDEN_NEIN)
                    || actualZustand.equals(LunchMessageZustand.ANMELDUNG_FEHLGESCHLAGEN)) {
                    lunchMessageProtocol.removeFlowForUser(actualUser);
                    return;
                }
            }
            return;
        }

        if (lunchFlow != null && lunchFlow.getClass().equals(LunchLogoutFlow.class)) {
            LunchMessageZustand actualZustand = lunchFlow.modifyFlowForUser(incomeMessage, actualUser);
            if (actualZustand != null) {
                if (actualZustand.equals(LunchMessageZustand.ABMELDEN_ERFOLGREICH)
                    || actualZustand.equals(LunchMessageZustand.ABMELDEN_NEIN)
                    || actualZustand.equals(LunchMessageZustand.ABMELDEN_FEHLGESCHLAGEN)) {
                    lunchMessageProtocol.removeFlowForUser(actualUser);
                    this.setSignedInNumber(0);
                    return;
                }
            }
            return;
        }

        if (textHandler.containsHelpCommand(incomeMessage)) {
            messageRecieverBase.sendMessageHtml(textHandler.getHelpText());
            return;
        }
    }

    private void handleMittagessenInfoMessage(final String incomeMessage, final String actualUser, final boolean login) throws JSONException, ParseException {
        Weekdays weekday = Weekdays.getEnumForText(incomeMessage);
        if (weekday.isWeekend()) {
            messageRecieverBase.sendMessageHtmlError("<b>Am " + weekday.getText() + " gibt es kein Mittagessen!</b>");
        } else {
            List<Lunch> lunchList = this.getLunchlist(weekday);
            if (lunchList.size() > 0) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("<b>Mittagessen " + weekday.getText() + "</b><br>");
                stringBuilder.append(this.createLunchOverview(lunchList, actualUser));
                messageRecieverBase.sendMessageHtml(stringBuilder.toString());
                if (!this.isLunchesClosed && this.signedInNumber == 0 && login) {
                    LunchFlow lunchLoginFlow = new LunchLoginFlow(messageRecieverBase, textHandler, superlunchRequestHandler);
                    lunchLoginFlow.modifyFlowForUser(incomeMessage, actualUser);
                    lunchMessageProtocol.addFlowForUser(actualUser, lunchLoginFlow);
                }
                if (!this.isLunchesClosed && (this.signedInNumber != 0 || !login)) {
                    LunchFlow lunchLogoutFlow = new LunchLogoutFlow(messageRecieverBase, textHandler, superlunchRequestHandler, signedInNumber);
                    lunchLogoutFlow.modifyFlowForUser(incomeMessage, actualUser);
                    lunchMessageProtocol.addFlowForUser(actualUser, lunchLogoutFlow);
                }
            } else {
                messageRecieverBase.sendMessageHtmlError(actualUser, textHandler.getOverviewErrorText());
            }
        }
    }

    private List<Lunch> getLunchlist(final Weekdays day) {
        return superlunchRequestHandler.fetchFilteredLunchFromConfluence(day);
    }

    private String createLunchOverview(final List<Lunch> lunchList, final String actualUser) {
        this.lunchesClosed = 0;
        StringBuilder stringBuilder = new StringBuilder();
        for (final Lunch lunch : lunchList) {
            stringBuilder.append(this.createMittagessenMessage(lunch, actualUser));
        }
        this.setIsLunchesClosed(lunchList);
        return stringBuilder.toString();
    }

    private String createMittagessenMessage(final Lunch lunch, final String actualUser) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<li>");
        if (!lunch.isClosed()) {
            if (this.checkIsUserSignIn(lunch, actualUser)) {
                stringBuilder.append("[<b>angemeldet(" + lunch.getId() + ")</b>] - " + lunch.getTitle() + " ");
                this.setSignedInNumber(lunch.getId());
            } else {
                stringBuilder.append("[Essennummer: <b>" + lunch.getId() + "</b>] - " + lunch.getTitle() + " ");
            }
        } else {
            stringBuilder.append("[<b>geschlossen</b>] - " + lunch.getTitle() + " ");
            this.setLunchesCounterClosed();
        }
        stringBuilder.append("(" + lunch.getCreatorName() + ", " + this.convertVeggyValue(lunch.isVeggy()) + ", " + "<a href=\"" + lunch.getDetailLink() + "\">Details</a>)");
        stringBuilder.append("</li>");
        return stringBuilder.toString();
    }

    private boolean checkIsUserSignIn(final Lunch lunch, final String actualUser) {
        Participant[] participants = lunch.getParticipants();
        for (Participant participant : participants) {
            if (participant.getName().equals(actualUser)) {
                return true;
            }
        }
        return false;
    }

    private String convertVeggyValue(final boolean value) {
        return (value) ? "vegetarisch" : "nicht vegetarisch";
    }

    private void setLunchesCounterClosed() {
        this.lunchesClosed++;
    }

    private void setIsLunchesClosed(final List<Lunch> lunchList) {
        if (lunchList.size() == this.lunchesClosed) {
            this.isLunchesClosed = true;
        } else {
            this.isLunchesClosed = false;
        }
    }

    private void setSignedInNumber(final int value) {
        this.signedInNumber = value;
    }

}
