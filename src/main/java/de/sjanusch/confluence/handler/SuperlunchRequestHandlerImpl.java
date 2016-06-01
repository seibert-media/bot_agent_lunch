package de.sjanusch.confluence.handler;

import com.google.inject.Inject;
import de.sjanusch.confluence.rest.SuperlunchRestClient;
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
import java.util.TimeZone;

/**
 * Created by Sandro Janusch
 * Date: 13.05.16
 * Time: 19:42
 */
public class SuperlunchRequestHandlerImpl implements SuperlunchRequestHandler {

    public static final Logger logger = LoggerFactory.getLogger(SuperlunchRequestHandlerImpl.class);

    private final SuperlunchRestClient superlunchRestClient;

    @Inject
    public SuperlunchRequestHandlerImpl(final SuperlunchRestClient superlunchRestClient) {
        this.superlunchRestClient = superlunchRestClient;
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
        List<Lunch> filteredLunches = new LinkedList<Lunch>();
        try {
            List<Lunch> lunches = superlunchRestClient.superlunchRestApiGet();
            if (lunches != null) {
                for (Lunch lunch : lunches) {
                    if (this.calculateDay(day) != null && this.isLunchAtDate(lunch, this.calculateDay(day))) {
                        filteredLunches.add(lunch);
                    }
                }
            }
        } catch (ParseException e) {
            logger.error("Fehler beim Confluence Rest-Call: fetchFilteredLunchFromConfluence");
            logger.error(e.getMessage());
        }
        return filteredLunches;
    }

    private boolean isLunchAtDate(final Lunch lunch, final Date today) throws ParseException {
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date todayWithZeroTime = formatter.parse(formatter.format(today));
        Date lunchDate = formatter.parse(formatter.format(this.getDateForString(lunch.getDate())));
        if (lunchDate.compareTo(todayWithZeroTime) == 0) {
            return true;
        }
        return false;
    }

    private Date calculateDay(final Weekdays day) {
        final Date now = new Date();
        Calendar cal = Calendar.getInstance(Locale.GERMAN);
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

    private Date getDateForString(final String time) {
        Calendar cal = Calendar.getInstance(Locale.GERMAN);
        cal.setTimeInMillis(Long.valueOf(time));
        TimeZone t = cal.getTimeZone();

        if (!t.getID().equals("Europe/Berlin")) {
            //cal.add(Calendar.DAY_OF_WEEK, 1);
        }
        return cal.getTime();
    }

    private Calendar nextDayOfWeek(int dow) {
        Calendar date = Calendar.getInstance();
        int diff = dow - date.get(Calendar.DAY_OF_WEEK);
        if (!(diff > 0)) {
            diff += 7;
        }
        date.add(Calendar.DAY_OF_MONTH, diff);
        return date;
    }
}
