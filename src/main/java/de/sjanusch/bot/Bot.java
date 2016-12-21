package de.sjanusch.bot;

import de.sjanusch.runner.RunnableBot;

import java.io.IOException;

public interface Bot extends RunnableBot {

  @Override
  void run();

  String getNickname() throws IOException;

  String getPassword() throws IOException;

  String getUsername() throws IOException;

}
