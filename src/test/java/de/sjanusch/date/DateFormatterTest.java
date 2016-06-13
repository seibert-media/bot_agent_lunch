package de.sjanusch.date;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class DateFormatterTest {

  @Test
  public void testFormatDate() throws Exception {
    DateFormatter dateFormatter = new DateFormatter();
    assertThat(dateFormatter.formatDate("1465768800000"), is("2016-06-13"));
    assertThat(dateFormatter.formatDate("1465855200000"), is("2016-06-14"));
  }
}
