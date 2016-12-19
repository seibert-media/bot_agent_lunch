package de.sjanusch;

import com.github.brainlag.nsq.NSQConsumer;
import com.github.brainlag.nsq.NSQMessage;
import com.github.brainlag.nsq.lookup.DefaultNSQLookup;
import com.github.brainlag.nsq.lookup.NSQLookup;
import com.google.inject.Inject;
import de.sjanusch.configuration.BotConfiguration;
import de.sjanusch.configuration.NSQConfiguration;
import de.sjanusch.listener.LunchMessageRecieveListener;
import de.sjanusch.listener.LunchPrivateMessageRecieveListener;
import de.sjanusch.model.nsq.NsqPrivateMessage;
import de.sjanusch.model.nsq.NsqPublicMessage;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;

/**
 * Created by Sandro Janusch
 * Date: 14.12.16
 * Time: 14:15
 */
public class NSQ implements Runnable {

  private static final Logger logger = LoggerFactory.getLogger(NSQ.class);

  private final NSQConfiguration nsqConfiguration;

  private final BotConfiguration botConfiguration;

  private final LunchMessageRecieveListener lunchMessageRecieveListener;

  private final LunchPrivateMessageRecieveListener lunchPrivateMessageRecieveListener;

  private final ObjectMapper mapper = new ObjectMapper();

  @Inject
  public NSQ(final NSQConfiguration nsqConfiguration, final BotConfiguration botConfiguration, final LunchMessageRecieveListener lunchMessageRecieveListener, final LunchPrivateMessageRecieveListener lunchPrivateMessageRecieveListener) {
    this.nsqConfiguration = nsqConfiguration;
    this.botConfiguration = botConfiguration;
    this.lunchMessageRecieveListener = lunchMessageRecieveListener;
    this.lunchPrivateMessageRecieveListener = lunchPrivateMessageRecieveListener;
  }

  @Override
  public void run() {
    this.runNsqPublic();
    this.runNsqPrivate();
  }

  private void runNsqPublic() {
    final Thread nsqPublic = this.nsqPublic();
    try {
      nsqPublic.join();
    } catch (InterruptedException e) {
      logger.error("InterruptedException: " + e.getMessage());
    }
    nsqPublic.start();
    logger.debug("NSQ Public started");
  }

  private void runNsqPrivate() {
    final Thread nsqPrivate = this.nsqPrivate();
    try {
      nsqPrivate.join();
    } catch (InterruptedException e) {
      logger.error("InterruptedException: " + e.getMessage());
    }
    nsqPrivate.start();
    logger.debug("NSQ Private started");
  }

  private Thread nsqPublic() {
    Thread threadNsq = new Thread() {

      @Override
      public void run() {
        startNsqLookupPublic();
      }
    };
    return threadNsq;
  }

  private Thread nsqPrivate() {
    Thread threadNsq = new Thread() {

      @Override
      public void run() {
        startNsqLookupPrivate();
      }
    };
    return threadNsq;
  }

  private void startNsqLookupPublic() {
    try {
      final NSQLookup lookup = new DefaultNSQLookup();
      lookup.addLookupAddress(nsqConfiguration.getNSQLookupAdress(), nsqConfiguration.getNSQLookupAdressPort());
      final NSQConsumer consumer = new NSQConsumer(lookup, nsqConfiguration.getNsqPublicTopicName(), botConfiguration.getBotNickname(), (message) -> {
        try {
          if (messageToString(message).equals("ping")) {
            logger.debug(nsqConfiguration.getNsqPublicTopicName() + " Queue is a life");
            finishMessage(message, true);
          } else {
            logger.debug("received message: " + nsqConfiguration.getNsqPublicTopicName() + ": " + messageToString(message));
            final NsqPublicMessage nsqPublicMessage = mapper.readValue(messageToString(message), NsqPublicMessage.class);
            if (nsqPublicMessage.getText() != null && nsqPublicMessage.getFullName() != null && nsqPublicMessage.getRoom() != null) {
              finishMessage(message, lunchMessageRecieveListener.handleMessage(nsqPublicMessage.getText(), nsqPublicMessage.getFullName(), nsqPublicMessage.getRoom()));
            } else {
              finishMessage(message, true);
            }
          }
        } catch (ParseException e) {
          logger.error("ParseException: " + e.getMessage());
        } catch (IOException e) {
          logger.error("IOException: " + e.getMessage());
        }
      });
      consumer.start();
    } catch (IOException e) {
      logger.error("IOException: " + e.getMessage());
    }
  }

  private void startNsqLookupPrivate() {
    try {
      final NSQLookup lookup = new DefaultNSQLookup();
      lookup.addLookupAddress(nsqConfiguration.getNSQLookupAdress(), nsqConfiguration.getNSQLookupAdressPort());
      final NSQConsumer consumer = new NSQConsumer(lookup, nsqConfiguration.getNsqPrivateTopicName(), botConfiguration.getBotNickname(), (message) -> {
        try {
          if (messageToString(message).equals("ping")) {
            logger.debug(nsqConfiguration.getNsqPrivateTopicName() + " Queue is a life:");
            finishMessage(message, true);
          } else {
            logger.debug("received message " + nsqConfiguration.getNsqPrivateTopicName() + ": " + messageToString(message));
            final NsqPrivateMessage nsqPrivateMessage = mapper.readValue(messageToString(message), NsqPrivateMessage.class);
            if (nsqPrivateMessage.getText() != null && nsqPrivateMessage.getFullName() != null) {
              finishMessage(message, lunchPrivateMessageRecieveListener.handleMessage(nsqPrivateMessage.getText(), nsqPrivateMessage.getFullName()));
            } else {
              finishMessage(message, true);
            }
          }
        } catch (ParseException e) {
          logger.error("ParseException: " + e.getMessage());
        } catch (IOException e) {
          logger.error("IOException: " + e.getMessage());
        }
      });
      consumer.start();
    } catch (IOException e) {
      logger.error("IOException: " + e.getMessage());
    }
  }

  private String messageToString(final NSQMessage message) {
    return new String(message.getMessage(), StandardCharsets.UTF_8);
  }

  private void finishMessage(final NSQMessage message, final boolean result) {
    if (result) {
      message.finished();
    } else {
      message.requeue();
    }
  }
}

