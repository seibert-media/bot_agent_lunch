package de.sjanusch.configuration;

import java.io.IOException;

/**
 * Created by Sandro Janusch Date: 17.05.16 Time: 11:15
 */
public interface BotConfiguration {

  String getBotNickname() throws IOException;

  String getBotUsername() throws IOException;

  String getBotPassword() throws IOException;

  String getBotChatRoom() throws IOException;

  String getBotMentionName() throws IOException;

}
