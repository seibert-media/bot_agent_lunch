package de.sjanusch.date;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.TimeZone;

import javax.inject.Inject;

public class DateFormatter {

  @Inject
  public DateFormatter() {
  }

  public String formatDate(final String input) {
    final Calendar calendar = getCalendar(input);
    LocalDate date = calendar.getTime().toInstant().atZone(ZoneId.of("Europe/Paris")).toLocalDate();
    return date.toString();
  }

  private Calendar getCalendar(final String time) {

    final Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    cal.setTimeInMillis(Long.parseLong(time));
    return cal;
  }
}
