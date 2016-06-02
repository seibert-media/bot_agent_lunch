package de.sjanusch.guice;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class GuiceModuleTest {

  @Test
  public void testCreateInjector() throws Exception {
    final Injector injector = Guice.createInjector(new GuiceModule());
    assertThat(injector, is(notNullValue()));
  }
}
