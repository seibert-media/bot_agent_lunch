package de.sjanusch.networking;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

public interface Connection {

  void waitForEnd() throws InterruptedException;

  void connect() throws XMPPException;

  void disconnect() throws XMPPException;

  boolean isConnected();

  XMPPConnection getXmpp();
}
