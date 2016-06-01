package de.sjanusch.bot;

import com.google.inject.Inject;
import de.sjanusch.configuration.BotConfiguration;
import de.sjanusch.eventsystem.EventSystem;
import de.sjanusch.listener.LuncheMessageRecieveListener;
import de.sjanusch.listener.MessageRecieveListener;
import de.sjanusch.model.hipchat.Room;
import de.sjanusch.networking.Connection;
import org.jivesoftware.smack.XMPPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class BotImpl implements Bot {

    private static final Logger logger = LoggerFactory.getLogger(BotImpl.class);

    private Room selected;

    private final EventSystem eventSystem;

    private final Connection connection;

    private final MessageRecieveListener messageRecieveListener;

    private final LuncheMessageRecieveListener luncheMessageRecieveListener;

    private final BotConfiguration botConfiguration;

    @Inject
    public BotImpl(final EventSystem eventSystem, final Connection connection, final MessageRecieveListener messageRecieveListener, final LuncheMessageRecieveListener luncheMessageRecieveListener, final BotConfiguration botConfiguration) {
        this.eventSystem = eventSystem;
        this.connection = connection;
        this.messageRecieveListener = messageRecieveListener;
        this.luncheMessageRecieveListener = luncheMessageRecieveListener;
        this.botConfiguration = botConfiguration;
    }

    @Override
    public void run() {
        //this.eventSystem.registerEvents(messageRecieveListener);
        this.eventSystem.registerEvents(luncheMessageRecieveListener);
        try {
            connection.connect();
            connection.login(this.getUsername(), this.getPassword());
            this.joinRoom();
            logger.debug("Joined " + getSelectedRoom().getXMPPName() + " !");
        } catch (XMPPException e) {
            logger.error("Error during join Room");
            logger.error(e.getMessage());
        } catch (Exception e) {
            logger.error("Error Connection");
            logger.error(e.getMessage());
        }
    }

    public void joinRoom() throws XMPPException, IOException {
        this.connection.joinRoom(this.getBotroom(), this.getNickname());
        selected = connection.findRoom(this.getBotroom());
    }

    @Override
    public Room getSelectedRoom() {
        return selected;
    }

    public String getBotroom() throws IOException {
        return botConfiguration.getBotChatRoom();
    }

    public String getNickname() throws IOException {
        return botConfiguration.getBotNickname();
    }

    public String getPassword() throws IOException {
        return botConfiguration.getBotPassword();
    }

    public String getUsername() throws IOException {
        return botConfiguration.getBotUsername();
    }
}
