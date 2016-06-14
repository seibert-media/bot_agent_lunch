package de.sjanusch.listener;

import com.google.inject.Inject;
import de.sjanusch.confluence.handler.SuperlunchRequestHandler;
import de.sjanusch.model.Weekdays;
import de.sjanusch.model.superlunch.Lunch;
import de.sjanusch.model.superlunch.Participant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by Sandro Janusch
 * Date: 02.06.16
 * Time: 23:48
 */
public class LunchListenerHelperImpl implements LunchListenerHelper {

  private static final Logger logger = LoggerFactory.getLogger(LunchListenerHelperImpl.class);

  private final String[][] UMLAUT_REPLACEMENTS = {{new String("Ä"), "Ae"}, {new String("Ü"), "Ue"}, {new String("Ö"), "Oe"}, {new String("ä"), "ae"}, {new String("ü"), "ue"}, {new String("ö"), "oe"}, {new String("ß"), "ss"}};

  private final SuperlunchRequestHandler superlunchRequestHandler;

  private int lunchesClosed = 0;

  private boolean isLunchesClosed = false;

  private int signedInNumber = 0;

  @Inject
  public LunchListenerHelperImpl(final SuperlunchRequestHandler superlunchRequestHandler) {
    this.superlunchRequestHandler = superlunchRequestHandler;
  }

  @Override
  public List<Lunch> getLunchlist(final Weekdays day) {
    return superlunchRequestHandler.fetchFilteredLunchFromConfluence(day);
  }

  @Override
  public String convertNames(final String from) {
    if (from.toLowerCase().trim().contains("paul herwarth von bittenfeld")) {
      return "pherwarth";
    }
    if (from.toLowerCase().trim().contains("lennart vn")) {
      return "lvniebelschuetz";
    }
    final String cleanedString = this.replaceUmlaute(from);
    final String[] names = cleanedString.split(" ");
    if (names.length > 1) {
      return names[0].toLowerCase().charAt(0) + names[names.length - 1].toLowerCase();
    }
    return names[0];
  }

  @Override
  public String createLunchOverview(final List<Lunch> lunchList, final String actualUser) {
    this.lunchesClosed = 0;
    final StringBuilder stringBuilder = new StringBuilder();
    for (final Lunch lunch : lunchList) {
      stringBuilder.append(this.createMittagessenMessage(lunch, actualUser));
    }
    this.setIsLunchesClosed(lunchList);
    return stringBuilder.toString();
  }

  @Override
  public boolean isLunchesClosed() {
    return isLunchesClosed;
  }

  @Override
  public int getSignedInNumber() {
    return signedInNumber;
  }

  @Override
  public void setSignedInNumber(final int value) {
    this.signedInNumber = value;
  }

  @Override
  public SuperlunchRequestHandler getSuperlunchRequestHandler() {
    return superlunchRequestHandler;
  }

  private String createMittagessenMessage(final Lunch lunch, final String actualUser) {
    final StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("<li>");
    if (!lunch.isClosed()) {
      if (this.checkIsUserSignIn(lunch, actualUser)) {
        stringBuilder.append("[<b>angemeldet</b>] - " + lunch.getTitle() + " ");
        this.setSignedInNumber(lunch.getId());
      } else {
        stringBuilder.append("[Essen-ID: <b>" + lunch.getId() + "</b>] - " + lunch.getTitle() + " ");
      }
    } else {
      stringBuilder.append("[<b>geschlossen</b>] - " + lunch.getTitle() + " ");
      this.setLunchesCounterClosed();
    }
    final String description = (lunch.getDescription() == "" || lunch.getDescription() == null) ? "" : lunch.getDescription() + ", ";
    stringBuilder.append("(" + lunch.getCreatorName() + ", " + this.convertVeggyValue(lunch.isVeggy()) + ", " + description + lunch.getFormattedPrice() + "&euro;)");
    stringBuilder.append("</li>");
    return stringBuilder.toString();
  }

  private boolean checkIsUserSignIn(final Lunch lunch, final String actualUser) {
    final Participant[] participants = lunch.getParticipants();
    for (final Participant participant : participants) {
      if (participant.getName().equals(actualUser)) {
        return true;
      }
    }
    return false;
  }

  private String convertVeggyValue(final boolean value) {
    return (value) ? "vegetarisch" : "nicht vegetarisch";
  }

  private void setLunchesCounterClosed() {
    this.lunchesClosed++;
  }

  private void setIsLunchesClosed(final List<Lunch> lunchList) {
    if (lunchList.size() == this.lunchesClosed) {
      this.isLunchesClosed = true;
    } else {
      this.isLunchesClosed = false;
    }
  }

  private String replaceUmlaute(String orig) {
    String result = orig;
    for (int i = 0; i < UMLAUT_REPLACEMENTS.length; i++) {
      result = result.replace(UMLAUT_REPLACEMENTS[i][0], UMLAUT_REPLACEMENTS[i][1]);
    }
    return result;
  }
}
