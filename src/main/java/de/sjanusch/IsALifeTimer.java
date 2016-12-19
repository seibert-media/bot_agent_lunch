package de.sjanusch;

import com.github.brainlag.nsq.NSQProducer;
import com.github.brainlag.nsq.exceptions.NSQException;
import com.google.inject.Inject;
import de.sjanusch.configuration.NSQConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeoutException;

/**
 * Created by Sandro Janusch
 * Date: 15.12.16
 * Time: 13:27
 */
public class IsALifeTimer implements Runnable {

  private static final Logger logger = LoggerFactory.getLogger(LunchMessageReminder.class);

  private final NSQConfiguration nsqConfiguration;

  @Inject
  public IsALifeTimer(final NSQConfiguration nsqConfiguration) {
    this.nsqConfiguration = nsqConfiguration;
  }

  @Override
  public void run() {
    final Thread isALifeTimerThread = createIsALifeTimerThread();
    try {
      isALifeTimerThread.join();
    } catch (InterruptedException e) {
      logger.error("InterruptedException: " + e.getMessage());
    }
    isALifeTimerThread.start();
    logger.debug("Is alife Service started");
  }

  private Thread createIsALifeTimerThread() {
    Thread thread = new Thread() {

      @Override
      public void run() {
        startIsALifeTimer();
      }
    };
    return thread;
  }

  private void startIsALifeTimer() {
    final Timer timer = new Timer("IsALifeTimer");
    final TimerTask timerTask = new TimerTask() {

      @Override
      public void run() {
        try {
          final String ping = "ping";
          final NSQProducer producer = new NSQProducer();
          producer.addAddress(nsqConfiguration.getNSQAdress(), nsqConfiguration.getNSQAdressPort()).start();
          logger.debug("Ping Public and Private Topic ...");
          producer.produce("PublicChat", ping.getBytes());
          producer.produce("PrivateChat", ping.getBytes());
        } catch (NSQException e) {
          logger.error("NSQException " + e.getMessage());
        } catch (TimeoutException e) {
          logger.error("TimeoutException " + e.getMessage());
        } catch (IOException e) {
          logger.error("IOException " + e.getMessage());
        }
      }
    };
    timer.scheduleAtFixedRate(timerTask, 0, 5 * 60 * 1000);
  }
}
