package de.sjanusch.listener;

/**
 * Created by Sandro Janusch
 * Date: 18.05.16
 * Time: 20:45
 */
public interface MessageRecieverBase {

    boolean isMessageFromBot(final String from);

    void sendMessageText(final String text);

    void sendMessageText(final String user, final String text);

    void sendMessageHtml(final String text);

    void sendMessageHtmlError(final String text);

    void sendMessageHtmlSucess(final String text);

    String convertNames(final String from);

    boolean isMessageForBot(final String message);

    void sendMessageHtmlError(final String user, final String text);

    void sendMessageHtmlSucess(final String user, final String text);
}
