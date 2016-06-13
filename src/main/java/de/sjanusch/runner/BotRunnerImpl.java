package de.sjanusch.runner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import de.sjanusch.networking.Connection;

public class BotRunnerImpl implements BotRunner {

  private static final Logger logger = LoggerFactory.getLogger(BotRunnerImpl.class);

  private final Connection connection;

  @Inject
  public BotRunnerImpl(final Connection connection) {
    this.connection = connection;
  }

  @Override
  public void runBot(final RunnableBot bot) {
    try {
      logger.debug("run bot");
      bot.run();
      logger.debug("wait for end");
      connection.waitForEnd();
      logger.debug("end reached => close");
    } catch (final InterruptedException e) {
      logger.warn(e.getClass().getName(), e);
    }
  }

}
