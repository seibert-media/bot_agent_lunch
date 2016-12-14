package de.sjanusch.networking;

import de.sjanusch.networking.exceptions.LoginException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smackx.muc.MultiUserChat;

import java.io.IOException;

/**
 * Created by Sandro Janusch
 * Date: 02.06.16
 * Time: 14:07
 */
public interface ChatClient {

  MultiUserChat joinChat(final XMPPConnection xmpp, final String room, final String user, final String password);

  boolean login(final XMPPConnection xmpp, String username, String password) throws LoginException, IOException;

  void startPrivateChat(final String username, MultiUserChat chat);

}
