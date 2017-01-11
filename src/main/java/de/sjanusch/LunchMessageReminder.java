package de.sjanusch;

import com.google.inject.Inject;
import de.sjanusch.protocol.LunchMessageProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Sandro Janusch
 * Date: 15.12.16
 * Time: 11:21
 */
public class LunchMessageReminder implements Runnable {

  private static final Logger logger = LoggerFactory.getLogger(LunchMessageReminder.class);

  private final LunchMessageProtocol lunchMessageProtocol;

  @Inject
  public LunchMessageReminder(final LunchMessageProtocol lunchMessageProtocol) {
    this.lunchMessageProtocol = lunchMessageProtocol;
  }

  @Override
  public void run() {
    final Thread lunchReminder = lunchReminder();
    try {
      lunchReminder.join();
    } catch (InterruptedException e) {
      logger.error("InterruptedException: " + e.getMessage());
    }
    lunchReminder.start();
    logger.debug("Lunchbot Reminder started");
  }

  private Thread lunchReminder() {
    Thread thread = new Thread() {

      @Override
      public void run() {
        startLunchReminder();
      }
    };
    return thread;
  }

  private void startLunchReminder() {
    final Timer timer = new Timer("lunchtimer");
    final TimerTask timerTask = new TimerTask() {

      @Override
      public void run() {
        lunchMessageProtocol.remindUser();
      }
    };
    timer.scheduleAtFixedRate(timerTask, 1000, 5 * 60 * 1000);
  }
}
