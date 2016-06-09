package de.sjanusch;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.benjaminborbe.bot.agent.Address;
import de.benjaminborbe.bot.agent.Runner;

public class Webhook implements Runnable {

  private static final Logger logger = LoggerFactory.getLogger(Webhook.class);

  private final Runner instance;

  private final String botname = "lunch";

  @Inject
  public Webhook(final Runner instance) {
    this.instance = instance;
  }

  @Override
  public void run() {
    try {
      logger.debug("bot started");
      final Address nsqdAddress = Address.fromEnv("NSQD_ADDRESS");
      final Address nsqLookupdAddress = Address.fromEnv("NSQ_LOOKUPD_ADDRESS");
      instance.run(nsqdAddress, nsqLookupdAddress, botname);
      logger.debug("bot finished");
    } catch (final Exception e) {
      logger.warn("bot failed", e);
    }
  }
}
