package de.sjanusch.networking.exceptions;

public class LoginException extends Exception {

  private static final long serialVersionUID = -8496783642684939232L;

  public LoginException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public LoginException(final String message) {
    super(message);
  }
}
