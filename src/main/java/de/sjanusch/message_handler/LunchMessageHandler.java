package de.sjanusch.message_handler;

import java.util.Collection;
import java.util.Collections;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.benjaminborbe.bot.agent.MessageHandler;
import de.benjaminborbe.bot.agent.Request;
import de.benjaminborbe.bot.agent.Response;

public class LunchMessageHandler implements MessageHandler {

  private static final Logger logger = LoggerFactory.getLogger(LunchMessageHandler.class);

  @Inject
  public LunchMessageHandler() {
  }

  @Override
  public Collection<Response> HandleMessage(final Request request) {
    logger.debug("handle message");
    if (request.getMessage() != null && request.getMessage().startsWith("/lunch")) {
      logger.debug("got hello => send response");
      final Response response = new Response();
      response.setMessage("hello from lunch");
      return Collections.singletonList(response);
    }
    return Collections.emptyList();
  }
}
