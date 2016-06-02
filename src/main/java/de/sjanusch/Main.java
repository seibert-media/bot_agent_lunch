package de.sjanusch;

import com.google.inject.Guice;
import com.google.inject.Injector;

import de.sjanusch.bot.Bot;
import de.sjanusch.guice.GuiceModule;
import de.sjanusch.runner.BotRunner;

public class Main {

  public static void main(final String[] args) {
    final Injector injector = Guice.createInjector(new GuiceModule());
    final Bot bot = injector.getInstance(Bot.class);
    final BotRunner botRunner = injector.getInstance(BotRunner.class);
    final Thread t = botRunner.runBotDesync(bot);
    t.start();
  }
}
