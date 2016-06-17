package de.sjanusch.model.hipchat;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Sandro Janusch
 * Date: 16.06.16
 * Time: 10:18
 */
@XmlRootElement
public class Error {

  private int code;

  private String message;

  private String type;

  public int getCode() {
    return code;
  }

  public void setCode(final int code) {
    this.code = code;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(final String message) {
    this.message = message;
  }

  public String getType() {
    return type;
  }

  public void setType(final String type) {
    this.type = type;
  }

  @Override
  public String toString() {
    return "Error{" +
      "code=" + code +
      ", message='" + message + '\'' +
      ", type='" + type + '\'' +
      '}';
  }
}
