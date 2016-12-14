package de.sjanusch.model.nsq;

import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Created by Sandro Janusch
 * Date: 14.12.16
 * Time: 16:35
 */

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class NsqPrivateMessage {

  private String fullName;

  private String text;

  public NsqPrivateMessage() {
  }

  public NsqPrivateMessage(final String fullName, final String text) {
    this.fullName = fullName;
    this.text = text;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(final String fullName) {
    this.fullName = fullName;
  }

  public String getText() {
    return text;
  }

  public void setText(final String text) {
    this.text = text;
  }
}
