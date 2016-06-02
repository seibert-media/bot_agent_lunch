package de.sjanusch.hipchat.handler;

import de.sjanusch.model.hipchat.HipchatMessage;

/**
 * Created by Sandro Janusch
 * Date: 13.05.16
 * Time: 20:05
 */
public interface HipchatRequestHandler {

    void sendMessage(final HipchatMessage chatMessage);

    void sendNotification(final HipchatMessage chatMessage);

    void sendPrivateMessage(final HipchatMessage chatMessage);

    void sendPrivateNotification(final HipchatMessage chatMessage);
}
