package de.sjanusch.bot;

import de.sjanusch.model.hipchat.Room;
import de.sjanusch.runner.RunnableBot;

import java.io.IOException;

public interface Bot extends RunnableBot {

  void run();

  Room getSelectedRoom();

  String getBotroom() throws IOException;

  String getNickname() throws IOException;

  String getPassword() throws IOException;

  String getUsername() throws IOException;

  void startPrivateChat(String username);
}
