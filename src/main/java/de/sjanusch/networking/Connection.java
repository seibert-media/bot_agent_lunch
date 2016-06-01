package de.sjanusch.networking;

import de.sjanusch.model.hipchat.Room;
import de.sjanusch.networking.exceptions.LoginException;
import org.jivesoftware.smack.XMPPException;

import java.io.IOException;

/**
 * Created by Sandro Janusch
 * Date: 13.05.16
 * Time: 06:27
 */
public interface Connection {

    void waitForEnd() throws InterruptedException;

    void connect() throws XMPPException;

    void login(String username, String password) throws LoginException;

    void joinRoom(String room, String nickname) throws XMPPException, IOException;

    Room findRoom(final String name);

}
