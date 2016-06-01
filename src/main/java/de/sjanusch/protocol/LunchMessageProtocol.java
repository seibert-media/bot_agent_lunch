package de.sjanusch.protocol;

import de.sjanusch.flow.LunchFlow;

/**
 * Created by Sandro Janusch Date: 18.05.16 Time: 21:39
 */
public interface LunchMessageProtocol {

  void addFlowForUser(final String username, final LunchFlow flow);

  void removeFlowForUser(final String username);

  LunchFlow getCurrentFlowForUser(final String username);

}
