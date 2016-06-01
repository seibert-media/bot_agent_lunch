package de.sjanusch.utils;

public enum NotificationType {

    HTML("html"),

    TEXT("text");
    
    String type;
    NotificationType(String type) { this.type = type; }
    
    public String getType() { return type; }
}