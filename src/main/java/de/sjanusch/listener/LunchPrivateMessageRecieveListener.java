package de.sjanusch.listener;

import java.io.IOException;
import java.text.ParseException;

/**
 * Created by Sandro Janusch Date: 18.05.16 Time: 20:32
 */
public interface LunchPrivateMessageRecieveListener {

  void handleMessage(final String message, final String from) throws ParseException, IOException;

}
