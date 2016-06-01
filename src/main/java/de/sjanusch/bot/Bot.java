package de.sjanusch.bot;

import de.sjanusch.model.hipchat.Room;

import java.io.IOException;

public interface Bot {

    void run();

    Room getSelectedRoom();

    String getBotroom() throws IOException;

    String getNickname() throws IOException;

    String getPassword() throws IOException;

    String getUsername() throws IOException;
}
