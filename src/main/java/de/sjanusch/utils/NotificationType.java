package de.sjanusch.utils;

public enum NotificationType {

  HTML("html"),

  TEXT("text");

  final String type;

  NotificationType(final String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }
}
