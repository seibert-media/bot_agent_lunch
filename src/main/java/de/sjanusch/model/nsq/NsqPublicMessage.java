package de.sjanusch.model.nsq;

import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Created by Sandro Janusch
 * Date: 14.12.16
 * Time: 15:14
 */

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class NsqPublicMessage {

  private String fullName;

  private String text;

  private String room;

  public NsqPublicMessage() {
  }

  public NsqPublicMessage(final String fullName, final String text, final String room) {
    this.fullName = fullName;
    this.text = text;
    this.room = room;
  }

  public String getFullName() {
    return fullName;
  }

  public String getRoom() {
    return room;
  }

  public String getText() {
    return text;
  }

  public void setFullName(final String fullName) {
    this.fullName = fullName;
  }

  public void setRoom(final String room) {
    this.room = room;
  }

  public void setText(final String text) {
    this.text = text;
  }
}
