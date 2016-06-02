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

  public String convertNames(final String from) {
    if (from.toLowerCase().trim().contains("paul herwarth von bittenfeld")) {
      return "pherwarth";
    }
    if (from.toLowerCase().trim().contains("lennart vn")) {
      return "lvniebelschuetz";
    }
    final String[] names = from.split(" ");
    if (names.length > 1) {
      return names[0].toLowerCase().charAt(0) + names[names.length - 1].toLowerCase();
    }
    return names[0];
  }
}
