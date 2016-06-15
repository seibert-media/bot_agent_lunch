package de.sjanusch;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.benjaminborbe.bot.agent.Address;
import de.benjaminborbe.bot.agent.Runner;

public class Webhook implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(Webhook.class);

	private static final String BOT_NAME = "BOT_NAME";

	private static final String NSQD_ADDRESS = "NSQD_ADDRESS";

	private static final String NSQ_LOOKUPD_ADDRESS = "NSQ_LOOKUPD_ADDRESS";

	private final Runner instance;

	@Inject
	public Webhook(final Runner instance) {
		this.instance = instance;
	}

	@Override
	public void run() {
		try {
			logger.debug("starting webhook");
			final Address nsqdAddress = Address.fromEnv(NSQD_ADDRESS);
			final Address nsqLookupdAddress = Address.fromEnv(NSQ_LOOKUPD_ADDRESS);
			final String bot_name = System.getenv(BOT_NAME);
			if (bot_name == null || bot_name.isEmpty()) {
				logger.warn("env {} missing", BOT_NAME);
				return;
			}
			logger.debug("bot_name: {}", bot_name);
			instance.run(nsqdAddress, nsqLookupdAddress, bot_name);
			logger.debug("webhook started");
		} catch (final Exception e) {
			logger.warn("start webhook failed", e);
		}
	}
}
