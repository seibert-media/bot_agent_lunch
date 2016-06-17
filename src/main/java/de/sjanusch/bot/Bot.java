package de.sjanusch.bot;

import de.sjanusch.runner.RunnableBot;

import java.io.IOException;

public interface Bot extends RunnableBot {

  @Override
  void run();

  String getBotroom() throws IOException;

  String getNickname() throws IOException;

  String getPassword() throws IOException;

  String getUsername() throws IOException;

  void startPrivateChat(String username);

}
