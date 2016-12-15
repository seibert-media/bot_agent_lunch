package de.sjanusch.flow;

import java.util.Calendar;

/**
 * Created by Sandro Janusch Date: 23.05.16 Time: 11:53
 */
public interface LunchFlow {

  LunchMessageZustand modifyFlowForUser(final String incomeMessage, final String user);

  void flowReminder(final String user);

  Calendar getCal();
}
