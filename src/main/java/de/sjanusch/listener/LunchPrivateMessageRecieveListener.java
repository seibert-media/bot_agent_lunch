package de.sjanusch.listener;

import de.sjanusch.model.hipchat.HipchatUser;

import java.io.IOException;
import java.text.ParseException;

/**
 * Created by Sandro Janusch Date: 18.05.16 Time: 20:32
 */
public interface LunchPrivateMessageRecieveListener {

  boolean handleMessage(final String message, final HipchatUser hipchatUser) throws ParseException, IOException;

}
