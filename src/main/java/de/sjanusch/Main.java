package de.sjanusch;

import com.google.inject.Guice;
import com.google.inject.Injector;
import de.sjanusch.guice.GuiceModule;

public class Main {

  public static void main(final String[] args) {
    final Injector injector = Guice.createInjector(new GuiceModule());
    final Xmpp xmpp = injector.getInstance(Xmpp.class);
    final NSQ nsq = injector.getInstance(NSQ.class);
    final LunchMessageReminder lunchMessageReminder = injector.getInstance(LunchMessageReminder.class);
    final IsALifeTimer isALifeTimer = injector.getInstance(IsALifeTimer.class);
    xmpp.run();
    nsq.run();
    lunchMessageReminder.run();
    isALifeTimer.run();
  }

}
