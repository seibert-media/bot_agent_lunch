package de.sjanusch.flow;

/**
 * Created by Sandro Janusch Date: 23.05.16 Time: 20:20
 */
public class LunchFlowHelper {

  public String extractId(final String incomeMessage) {
    final String[] strings = incomeMessage.split(" ");
    for (final String s : strings) {
      try {
        final int i = Integer.parseInt(s);
        return String.valueOf(i);
      } catch (final Exception e) {

      }
    }
    return null;
  }
}
