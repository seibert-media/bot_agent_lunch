package de.sjanusch.date;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class DateFormatter {

  private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

  @Inject
  public DateFormatter() {
  }

  public String formatDate(final String date) {
    return format.format(getCalendar(date).getTime());
  }

  private Calendar getCalendar(final String time) {
    final Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    cal.setTimeInMillis(Long.parseLong(time));
    return cal;
  }
}
