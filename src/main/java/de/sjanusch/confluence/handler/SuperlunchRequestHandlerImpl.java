package de.sjanusch.confluence.handler;

import com.google.inject.Inject;
import de.sjanusch.confluence.rest.SuperlunchRestClient;
import de.sjanusch.date.DateFormatter;
import de.sjanusch.model.Weekdays;
import de.sjanusch.model.superlunch.Lunch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Sandro Janusch Date: 13.05.16 Time: 19:42
 */
public class SuperlunchRequestHandlerImpl implements SuperlunchRequestHandler {

  public static final Logger logger = LoggerFactory.getLogger(SuperlunchRequestHandlerImpl.class);

  private final SuperlunchRestClient superlunchRestClient;

  private final DateFormatter dateFormatter;

  @Inject
  public SuperlunchRequestHandlerImpl(final SuperlunchRestClient superlunchRestClient, final DateFormatter dateFormatter) {
    this.superlunchRestClient = superlunchRestClient;
    this.dateFormatter = dateFormatter;
  }

  @Override
  public boolean signInForLunch(final String id, final String username) {
    return superlunchRestClient.superlunchRestApiSignIn(id, username);
  }

  @Override
  public boolean signOutForLunch(final String id, final String username) {
    return superlunchRestClient.superlunchRestApiSignOut(id, username);
  }

  @Override
  public List<Lunch> fetchLunchFromConfluence() {
    return superlunchRestClient.superlunchRestApiGet();
  }

  @Override
  public List<Lunch> fetchFilteredLunchFromConfluence(final Weekdays day) {
    final List<Lunch> filteredLunches = new LinkedList<>();
    try {
      final List<Lunch> lunches = superlunchRestClient.superlunchRestApiGet();
      if (lunches != null) {
        logger.debug("got {} lunches", lunches.size());
        for (final Lunch lunch : lunches) {
          if (this.calculateDay(day) != null && this.isLunchAtDate(lunch, this.calculateDay(day))) {
            filteredLunches.add(lunch);
          }
        }
      } else {
        logger.debug("got no lunches");
      }
    } catch (final ParseException e) {
      logger.error("Fehler beim Confluence Rest-Call: fetchFilteredLunchFromConfluence", e);
    }
    logger.debug("return {} filtered lunches", filteredLunches.size());
    return filteredLunches;
  }

  private boolean isLunchAtDate(final Lunch lunch, final Date today) throws ParseException {
    final DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    final Date todayWithZeroTime = formatter.parse(formatter.format(today));
    final Date lunchDate = formatter.parse(this.getDateForString(lunch.getDate()));
    if (lunchDate.compareTo(todayWithZeroTime) == 0) {
      return true;
    }
    return false;
  }

  private Date calculateDay(final Weekdays day) {
    final Date now = new Date();
    final Calendar cal = Calendar.getInstance(Locale.GERMAN);
    cal.setTime(now);

    if (day.equals(Weekdays.TODAY)) {
      return cal.getTime();
    }

    if (day.equals(Weekdays.TOMMOROW)) {
      cal.add(Calendar.DAY_OF_WEEK, 1);
      return cal.getTime();
    }

    if (day.equals(Weekdays.MONDAY)) {
      return nextDayOfWeek(Calendar.MONDAY).getTime();
    }

    if (day.equals(Weekdays.TUESDAY)) {
      return nextDayOfWeek(Calendar.TUESDAY).getTime();
    }

    if (day.equals(Weekdays.WEDNESDAY)) {
      return nextDayOfWeek(Calendar.WEDNESDAY).getTime();
    }

    if (day.equals(Weekdays.THURSDAY)) {
      return nextDayOfWeek(Calendar.THURSDAY).getTime();
    }

    if (day.equals(Weekdays.FRIDAY)) {
      return nextDayOfWeek(Calendar.FRIDAY).getTime();
    }

    return null;
  }

  private String getDateForString(final String time) {
    return dateFormatter.formatDate(time) ;
  }

  private Calendar nextDayOfWeek(final int dow) {
    final Calendar date = Calendar.getInstance();
    int diff = dow - date.get(Calendar.DAY_OF_WEEK);
    if (!(diff > 0)) {
      diff += 7;
    }
    date.add(Calendar.DAY_OF_MONTH, diff);
    return date;
  }
}
