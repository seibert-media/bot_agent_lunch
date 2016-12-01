package de.sjanusch;

import de.sjanusch.bot.Bot;
import de.sjanusch.runner.BotRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Timer;
import java.util.TimerTask;

public class Xmpp implements Runnable {

  private static final Logger logger = LoggerFactory.getLogger(Xmpp.class);

  private final BotRunner botRunner;

  private final Bot bot;

  @Inject
  public Xmpp(final BotRunner botRunner, final Bot bot) {
    this.botRunner = botRunner;
    this.bot = bot;
  }

  @Override
  public void run() {
    TimerTask timerTask = new TimerTask() {

      public void run() {

        final Thread t = botRunner.runBotDesync(bot);
        try {
          t.join();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        t.start();
      }
    };

    Timer timer = new Timer("MyTimer");
    timer.scheduleAtFixedRate(timerTask, 0, 60 * 500);
  }
}
