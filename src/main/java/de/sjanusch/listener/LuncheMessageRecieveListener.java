package de.sjanusch.listener;

import de.sjanusch.eventsystem.Listener;
import de.sjanusch.eventsystem.events.model.MessageRecivedEvent;

/**
 * Created by Sandro Janusch Date: 18.05.16 Time: 20:32
 */
public interface LuncheMessageRecieveListener extends Listener {

  void messageEvent(final MessageRecivedEvent event);

}
