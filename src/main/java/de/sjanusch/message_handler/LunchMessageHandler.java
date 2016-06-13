package de.sjanusch.message_handler;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.benjaminborbe.bot.agent.MessageHandler;
import de.benjaminborbe.bot.agent.Request;
import de.benjaminborbe.bot.agent.Response;
import de.sjanusch.confluence.rest.SuperlunchRestClient;
import de.sjanusch.date.DateFormatter;
import de.sjanusch.model.superlunch.Lunch;

public class LunchMessageHandler implements MessageHandler {

  private static final Logger logger = LoggerFactory.getLogger(LunchMessageHandler.class);

  public static final String PREFIX = "/essen";

  private final SuperlunchRestClient superlunchRestClient;

  private final DateFormatter dateFormatter;

  @Inject
  public LunchMessageHandler(final SuperlunchRestClient superlunchRestClient, final DateFormatter dateFormatter) {
    this.superlunchRestClient = superlunchRestClient;
    this.dateFormatter = dateFormatter;
  }

  @Override
  public Collection<Response> HandleMessage(final Request request) {
    logger.debug("handle message");
    if (request.getMessage() == null || !request.getMessage().startsWith(PREFIX)) {
      logger.debug("message != {} => skip", PREFIX);
      return Collections.emptyList();
    }
    final List<Lunch> lunches = superlunchRestClient.superlunchRestApiGet();
    if (lunches == null) {
      logger.debug("lunches == null");
      return getResponses("get lunches failed");
    }
    logger.debug("got {} lunches", lunches.size());
    StringBuffer sb = new StringBuffer();
    boolean first = true;
    for (Lunch lunch : lunches) {
      if (first) {
        first = false;
      } else {
        sb.append("\n");
      }
      sb.append(dateFormatter.formatDate(lunch.getDate())).append(" ").append(lunch.getTitle());
    }
    return getResponses(sb.toString());
  }

  private Collection<Response> getResponses(final String message) {
    final Response response = new Response();
    response.setMessage(message);
    return Collections.singletonList(response);
  }

}
