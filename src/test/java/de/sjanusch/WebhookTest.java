package de.sjanusch;

import com.google.inject.Guice;
import com.google.inject.Injector;
import de.sjanusch.guice.GuiceModule;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;

public class WebhookTest {

  @Test
  public void testInject() throws Exception {
    final Injector injector = Guice.createInjector(new GuiceModule());
    assertThat(injector.getInstance(Webhook.class), is(notNullValue()));
  }
}
