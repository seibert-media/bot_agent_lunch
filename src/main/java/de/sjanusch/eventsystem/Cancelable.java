package de.sjanusch.eventsystem;

public interface Cancelable {

    boolean isCancelled();

    void setCancel(boolean cancel);

}

