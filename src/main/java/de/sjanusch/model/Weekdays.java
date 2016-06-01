package de.sjanusch.model;

/**
 * Created by Sandro Janusch
 * Date: 20.05.16
 * Time: 13:31
 */
public enum Weekdays {

    MONDAY("Montag"),
    TUESDAY("Dienstag"),
    WEDNESDAY("Mittwoch"),
    THURSDAY("Donnerstag"),
    FRIDAY("Freitag"),
    SATURDAY("Samstag"),
    SUNDAY("Sonntag"),
    TODAY("Heute"),
    TOMMOROW("Morgen");

    private String text;

    private Weekdays(final String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public static Weekdays getEnumForText(final String text) {
        for (Weekdays weekdays : Weekdays.values()) {
            if (text.contains(weekdays.getText().toLowerCase())) {
                return weekdays;
            }
        }
        return TODAY;
    }

    public boolean isWeekend() {
        if (this.equals(Weekdays.SATURDAY) || this.equals(Weekdays.SUNDAY)) {
            return true;
        }
        return false;
    }

}