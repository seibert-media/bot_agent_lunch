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
  public Thread runBotDesync(final RunnableBot bot) {
    return this.runBotDysync(bot);
  }

  private void run(final RunnableBot bot) {
    try {
			bot.run();
			connection.waitForEnd();
    } catch (final Exception e) {
      logger.warn(e.getClass().getName(), e);
    }
  }

  private Thread runBotDysync(final RunnableBot bot) {
    final Thread t = new Thread() {

      @Override
      public void run() {
        BotRunnerImpl.this.run(bot);
      }
    };
    return t;
  }
}
