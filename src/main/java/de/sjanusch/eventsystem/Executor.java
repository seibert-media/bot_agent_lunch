package de.sjanusch.eventsystem;

public interface Executor {

    void execute(Listener listen, Event event) throws Exception;
}

