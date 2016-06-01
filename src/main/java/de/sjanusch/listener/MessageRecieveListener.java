package de.sjanusch.listener;

import de.sjanusch.eventsystem.Listener;
import de.sjanusch.eventsystem.events.model.MessageRecivedEvent;

/**
 * Created by Sandro Janusch
 * Date: 13.05.16
 * Time: 20:43
 */
public interface MessageRecieveListener extends Listener {

    void messageEvent(final MessageRecivedEvent event);

}
