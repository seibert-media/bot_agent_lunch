package de.sjanusch.networking;

import com.google.inject.Inject;
import de.sjanusch.configuration.ChatConnectionConfiguration;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ConnectionImpl implements Connection, ConnectionListener {

    private static final Logger logger = LoggerFactory.getLogger(ConnectionImpl.class);

    private final ChatConnectionConfiguration chatConnectionConfiguration;

  private final XMPPConnection xmpp;

    public boolean connected;

    @Inject
    public ConnectionImpl(final ChatClient chatClient, final ChatConnectionConfiguration chatConnectionConfiguration) throws IOException {
        this.chatConnectionConfiguration = chatConnectionConfiguration;
        this.xmpp = new XMPPConnection(new ConnectionConfiguration(this.chatConnectionConfiguration.getXmppUrl(), this.chatConnectionConfiguration.getXmppPort()));
    }

  @Override
  public void connect() throws XMPPException {
        if (connected)
            return;
        xmpp.connect();
        xmpp.addConnectionListener(this);
        connected = true;
    }

  @Override
  public boolean isConnected() {
        return connected;
    }

    public void disconnect() {
        if (!connected)
            return;
        xmpp.disconnect();
        connected = false;
    }

    @Override
    public void connectionClosed() {
        connected = false;
    }

    @Override
  public void connectionClosedOnError(final Exception e) {
        connected = false;
    }

    @Override
  public void reconnectingIn(final int seconds) {

    }

    @Override
  public void reconnectionFailed(final Exception e) {
        if (connected)
            connected = false;
    }

    @Override
    public void reconnectionSuccessful() {
        if (!connected)
            connected = true;
    }

  @Override
  public synchronized void waitForEnd() throws InterruptedException {
        while (true) {
            if (!connected)
                break;
            super.wait(0L);
        }
    }

  @Override
  public XMPPConnection getXmpp() {
        return xmpp;
    }
}
