package de.sjanusch.listener;

/**
 * Created by Sandro Janusch Date: 18.05.16 Time: 20:45
 */
public interface PrivateMessageRecieverBase {

  void sendPrivateMessageText(final String text, final String username);

  void sendPrivateNotification(final String text, final String username);

  void sendPrivateNotificationError(final String text, final String username);

  void sendPrivateNotificationSucess(final String text, final String username);

  void sendMessageTextToRoom(final String text, final String roomId);

  boolean isMessageFromBot(final String from);

  boolean isMessageForBot(final String message);
}
