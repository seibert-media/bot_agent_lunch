package de.sjanusch;

import com.google.inject.Guice;
import com.google.inject.Injector;

import de.sjanusch.guice.GuiceModule;

public class Main {

  public static void main(final String[] args) {
    final Injector injector = Guice.createInjector(new GuiceModule());
    final Webhook webhook = injector.getInstance(Webhook.class);
    final Xmpp xmpp = injector.getInstance(Xmpp.class);
    webhook.run();
    // xmpp.run();
  }
}
