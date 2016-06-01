package de.sjanusch.protocol;

import java.util.HashMap;
import java.util.Map;

import de.sjanusch.flow.LunchFlow;

/**
 * Created by Sandro Janusch Date: 18.05.16 Time: 21:39
 */
public class LunchMessageProtocolImpl implements LunchMessageProtocol {

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

}
