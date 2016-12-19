package de.sjanusch;

import de.sjanusch.bot.Bot;
import de.sjanusch.runner.BotRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class Xmpp implements Runnable {

  private static final Logger logger = LoggerFactory.getLogger(Xmpp.class);

  private final BotRunner botRunner;

  private final Bot bot;

  @Inject
  public Xmpp(final BotRunner botRunner, final Bot bot) {
    this.botRunner = botRunner;
    this.bot = bot;
  }

  @Override
  public void run() {
    final Thread t = botRunner.runBotDesync(bot);
    try {
      t.join();
    } catch (InterruptedException e) {
      logger.error("InterruptedException: " + e.getMessage());
    }
    t.start();
    logger.debug("Lunchbot started");
  }

}

