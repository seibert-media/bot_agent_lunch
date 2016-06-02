package de.sjanusch;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

import de.sjanusch.guice.GuiceModule;

public class XmppTest {

  @Test
  public void testInject() throws Exception {
    final Injector injector = Guice.createInjector(new GuiceModule());
    assertThat(injector.getInstance(Xmpp.class), is(notNullValue()));
  }
}
