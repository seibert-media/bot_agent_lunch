package de.sjanusch.eventsystem;

/**
 * Created by Sandro Janusch
 * Date: 13.05.16
 * Time: 03:31
 */
public interface EventSystem {

    void registerEvents(Listener l);

    void callEvent(Event event);
}
