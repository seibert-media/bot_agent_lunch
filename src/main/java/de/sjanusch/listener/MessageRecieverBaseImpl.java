package de.sjanusch.listener;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import de.sjanusch.configuration.BotConfiguration;
import de.sjanusch.hipchat.handler.HipchatRequestHandler;
import de.sjanusch.model.hipchat.HipchatMessage;

/**
 * Created by Sandro Janusch
 * Date: 18.05.16
 * Time: 20:42
 */
public class MessageRecieverBaseImpl implements MessageRecieverBase {

    private static final Logger logger = LoggerFactory.getLogger(MessageRecieverBaseImpl.class);

    private final HipchatRequestHandler hipchatRequestHandler;

    private final BotConfiguration botConfiguration;

    @Inject
    public MessageRecieverBaseImpl(final HipchatRequestHandler hipchatRequestHandler, final BotConfiguration botConfiguration) {
        this.hipchatRequestHandler = hipchatRequestHandler;
        this.botConfiguration = botConfiguration;
    }

    public boolean isMessageFromBot(final String from) {
        try {
            if (from.contains("fyo")) {
                return true;
            }
            return from.toLowerCase().trim().equals(botConfiguration.getBotNickname().toLowerCase().trim());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void sendMessageNormalText(final String text) {
        hipchatRequestHandler.sendMessage(new HipchatMessage(text));
    }

    public void sendMessagePersonalText(final String user, final String text) {
        hipchatRequestHandler.sendMessage(new HipchatMessage("@" + user + " " + text));
    }

    public void sendMessageText(final String user, final String text) {
        if (text != null && user != null) {
            this.sendMessagePersonalText(user, text);
        }
    }

    public void sendMessageText(final String text) {
        if (text != null) {
            this.sendMessageNormalText(text);
        }
    }

    public void sendMessageHtml(final String text) {
        if (text != null) {
            hipchatRequestHandler.sendNotification(new HipchatMessage(text, "html"));
        }
    }

    public void sendMessageHtmlError(final String user, final String text) {
        if (text != null && user != null) {
            HipchatMessage hipchatMessage = new HipchatMessage("@" + user + " " + text);
            hipchatMessage.setColor("red");
            hipchatRequestHandler.sendNotification(hipchatMessage);
        }
    }

    public void sendMessageHtmlSucess(final String user, final String text) {
        if (text != null && user != null) {
            HipchatMessage hipchatMessage = new HipchatMessage("@" + user + " " + text);
            hipchatMessage.setColor("green");
            hipchatRequestHandler.sendNotification(hipchatMessage);
        }
    }

    public void sendMessageHtmlError(final String text) {
        if (text != null) {
            HipchatMessage hipchatMessage = new HipchatMessage(text);
            hipchatMessage.setColor("red");
            hipchatRequestHandler.sendNotification(hipchatMessage);
        }
    }

    public void sendMessageHtmlSucess(final String text) {
        if (text != null) {
            HipchatMessage hipchatMessage = new HipchatMessage(text);
            hipchatMessage.setColor("green");
            hipchatRequestHandler.sendNotification(hipchatMessage);
        }
    }

    public String convertNames(final String from) {
        if (from.toLowerCase().trim().contains("paul herwarth von bittenfeld")) {
            return "pherwarth";
        }
        if (from.toLowerCase().trim().contains("lennart vn")) {
            return "lvniebelschuetz";
        }
        String[] names = from.split(" ");
        if (names.length > 1) {
            return names[0].toLowerCase().charAt(0) + names[names.length - 1].toLowerCase();
        }
        return names[0];
    }

    public boolean isMessageForBot(final String message) {
        try {
            return message.toLowerCase().trim().contains("@" + botConfiguration.getBotMentionName().toLowerCase().trim());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}
