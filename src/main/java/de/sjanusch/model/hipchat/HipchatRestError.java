package de.sjanusch.model.hipchat;

/**
 * Created by Sandro Janusch
 * Date: 16.06.16
 * Time: 10:09
 */
public class HipchatRestError {

  private Error error;

  public Error getError() {
    return error;
  }

  public void setError(final Error error) {
    this.error = error;
  }

  @Override
  public String toString() {
    return "HipchatRestError{" +
      "error=" + error +
      '}';
  }
}
