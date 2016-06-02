package de.sjanusch.networking;

import de.sjanusch.configuration.ChatConnectionConfiguration;
import de.sjanusch.model.hipchat.Room;
import de.sjanusch.networking.exceptions.LoginException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smackx.muc.MultiUserChat;

import java.util.HashMap;

/**
 * Created by Sandro Janusch
 * Date: 02.06.16
 * Time: 14:07
 */
public interface ChatClient {

    ChatConnectionConfiguration getChatConnectionConfiguration();

    HashMap<Room, MultiUserChat> joinChat(final XMPPConnection xmpp, final String room, final String user, final String password);

    void login(final XMPPConnection xmpp, String username, String password) throws LoginException;
}
