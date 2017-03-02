package de.sjanusch.model.nsq;

import de.sjanusch.model.hipchat.HipchatUser;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Created by Sandro Janusch
 * Date: 14.12.16
 * Time: 16:35
 */

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class NsqPrivateMessage {

  private String text;

  private HipchatUser hipchatUser;

  public NsqPrivateMessage() {
  }

  public NsqPrivateMessage(final String text, HipchatUser hipchatUser) {
    this.text = text;
    this.hipchatUser = hipchatUser;
  }

  public String getText() {
    return text;
  }

  public void setText(final String text) {
    this.text = text;
  }

  public HipchatUser getHipchatUser() {
    return hipchatUser;
  }

  public void setHipchatUser(final HipchatUser hipchatUser) {
    this.hipchatUser = hipchatUser;
  }
}
