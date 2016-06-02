package de.sjanusch.listener;

import de.sjanusch.eventsystem.Listener;
import de.sjanusch.eventsystem.events.model.PrivateMessageRecivedEvent;

/**
 * Created by Sandro Janusch Date: 18.05.16 Time: 20:32
 */
public interface LunchPrivateMessageRecieveListener extends Listener {

  void messageEvent(final PrivateMessageRecivedEvent event);

}
