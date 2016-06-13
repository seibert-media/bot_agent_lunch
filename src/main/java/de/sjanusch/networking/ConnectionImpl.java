package de.sjanusch.networking;

import java.io.IOException;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import de.sjanusch.configuration.ChatConnectionConfiguration;

public class ConnectionImpl implements Connection, ConnectionListener {

  private static final Logger logger = LoggerFactory.getLogger(ConnectionImpl.class);

  private final ChatConnectionConfiguration chatConnectionConfiguration;

  private final XMPPConnection xmpp;

  private boolean connected;

  @Inject
  public ConnectionImpl(final ChatClient chatClient, final ChatConnectionConfiguration chatConnectionConfiguration) throws IOException {
    this.chatConnectionConfiguration = chatConnectionConfiguration;
    this.xmpp = new XMPPConnection(
        new ConnectionConfiguration(this.chatConnectionConfiguration.getXmppUrl(), this.chatConnectionConfiguration.getXmppPort()));
  }

  @Override
  public void connect() throws XMPPException {
    logger.debug("connect");
    if (connected) {
      logger.debug("already connected");
      return;
    }
    xmpp.connect();
    xmpp.addConnectionListener(this);
    connected = true;
  }

  @Override
  public boolean isConnected() {
    logger.debug("isConnected");
    return connected;
  }

  @Override
  public void disconnect() {
    logger.debug("disconnect");
    if (!connected) {
      logger.debug("not connected");
      return;
    }
    xmpp.disconnect();
    connected = false;
  }

  @Override
  public void connectionClosed() {
    logger.debug("connectionClosed");
    connected = false;
  }

  @Override
  public void connectionClosedOnError(final Exception e) {
    logger.debug("connectionClosedOnError", e);
    connected = false;
  }

  @Override
  public void reconnectingIn(final int seconds) {
    logger.debug("reconnectingIn {}", seconds);
  }

  @Override
  public void reconnectionFailed(final Exception e) {
    logger.debug("reconnectionFailed", e);
    connected = false;
  }

  @Override
  public void reconnectionSuccessful() {
    logger.debug("reconnectionSuccessful");
    connected = true;
  }

  @Override
  public synchronized void waitForEnd() throws InterruptedException {
    logger.debug("waitForEnd started");
    while (true) {
      if (!connected) {
        logger.debug("break wait for end");
        break;
      }
      super.wait(0L);
    }
    logger.debug("waitForEnd finished");
  }

  @Override
  public XMPPConnection getXmpp() {
    logger.debug("getXmpp");
    return xmpp;
  }
}
