package de.sjanusch.protocol;

import de.sjanusch.flow.LunchFlow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by Sandro Janusch Date: 18.05.16 Time: 21:39
 */
public class LunchMessageProtocolImpl implements LunchMessageProtocol {

  private static final Logger logger = LoggerFactory.getLogger(LunchMessageProtocolImpl.class);

  private final Map<String, LunchFlow> lunchProtocol = new HashMap<>();

  @Override
  public void addFlowForUser(final String username, final LunchFlow flow) {
    if (!lunchProtocol.containsKey(username)) {
      lunchProtocol.put(username, flow);
    }
  }

  @Override
  public void removeFlowForUser(final String username) {
    if (lunchProtocol.containsKey(username)) {
      lunchProtocol.remove(username);
    }
  }

  @Override
  public LunchFlow getCurrentFlowForUser(final String username) {
    if (lunchProtocol.containsKey(username)) {
      final LunchFlow lunchFlow = lunchProtocol.get(username);
      return lunchFlow;
    }
    return null;
  }

  @Override
  public void remindUser() {
    logger.debug("Open User Flows: " + this.lunchProtocol.size());
    for (Map.Entry<String, LunchFlow> entry : this.lunchProtocol.entrySet()) {
      String user = entry.getKey();
      LunchFlow flow = entry.getValue();
      if (this.isTimeRange(flow.getCal(), Calendar.getInstance(TimeZone.getTimeZone("UTC")))) {
        logger.debug("Remind Open User: " + user);
        flow.flowReminder(user);
      }
    }
  }

  private boolean isTimeRange(Calendar cal1, Calendar cal2) {
    if (cal1 == null || cal2 == null) {
      return false;
    }
    return (Math.abs(cal1.getTimeInMillis() - cal2.getTimeInMillis()) > 300000);
  }

}
