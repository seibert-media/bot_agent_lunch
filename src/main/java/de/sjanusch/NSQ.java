package de.sjanusch;

import com.github.brainlag.nsq.NSQConsumer;
import com.github.brainlag.nsq.lookup.DefaultNSQLookup;
import com.github.brainlag.nsq.lookup.NSQLookup;
import com.google.inject.Inject;
import de.sjanusch.configuration.BotConfiguration;
import de.sjanusch.configuration.NSQConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Created by Sandro Janusch
 * Date: 14.12.16
 * Time: 14:15
 */
public class NSQ implements Runnable {

  private static final Logger logger = LoggerFactory.getLogger(NSQ.class);

  private final NSQConfiguration nsqConfiguration;

  private final BotConfiguration botConfiguration;

  @Inject
  public NSQ(final NSQConfiguration nsqConfiguration, final BotConfiguration botConfiguration) {
    this.nsqConfiguration = nsqConfiguration;
    this.botConfiguration = botConfiguration;
  }

  @Override
  public void run() {
    Thread threadNsq = new Thread() {

      @Override
      public void run() {
        startNsqLookup();
      }
    };
    threadNsq.start();
  }

  private void startNsqLookup() {
    try {
      final NSQLookup lookup = new DefaultNSQLookup();
      lookup.addLookupAddress(nsqConfiguration.getNSQLookupAdress(), nsqConfiguration.getNSQLookupAdressPort());
      NSQConsumer consumer = new NSQConsumer(lookup, "PublicChat", botConfiguration.getBotNickname(), (message) -> {
        logger.debug("received: " + message);

        logger.debug("Test HAllo : " + new String(message.getMessage(), StandardCharsets.UTF_8));

        message.finished();
      });
      consumer.start();
    } catch (IOException e) {
      logger.error("IOException: " + e.getMessage());
    }
  }
}