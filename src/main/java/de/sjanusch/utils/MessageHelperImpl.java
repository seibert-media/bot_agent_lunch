package de.sjanusch.utils;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Sandro Janusch
 * Date: 02.06.16
 * Time: 23:48
 */
public class MessageHelperImpl implements MessageHelper {

  private static final Logger logger = LoggerFactory.getLogger(MessageHelperImpl.class);

  private final String[][] UMLAUT_REPLACEMENTS = {{new String("Ä"), "Ae"}, {new String("Ü"), "Ue"}, {new String("Ö"), "Oe"}, {new String("ä"), "ae"}, {new String("ü"), "ue"}, {new String("ö"), "oe"}, {new String("ß"), "ss"}};

  @Inject
  public MessageHelperImpl() {

  }

  @Override
  public String convertNames(final String from) {
    final String cleanedString = this.replaceUmlaute(from);
    final String[] names = cleanedString.split(" ");
    if (names.length > 1) {
      return names[0].toLowerCase().charAt(0) + names[names.length - 1].toLowerCase();
    }
    return names[0];
  }

  private String replaceUmlaute(String orig) {
    String result = orig;
    for (int i = 0; i < UMLAUT_REPLACEMENTS.length; i++) {
      result = result.replace(UMLAUT_REPLACEMENTS[i][0], UMLAUT_REPLACEMENTS[i][1]);
    }
    return result;
  }
}
