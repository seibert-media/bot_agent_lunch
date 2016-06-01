package de.sjanusch.bot;

import java.io.IOException;

import de.sjanusch.model.hipchat.Room;

public interface Bot {

    void run();

    Room getSelectedRoom();

    String getBotroom() throws IOException;

    String getNickname() throws IOException;

    String getPassword() throws IOException;

    String getUsername() throws IOException;
}
