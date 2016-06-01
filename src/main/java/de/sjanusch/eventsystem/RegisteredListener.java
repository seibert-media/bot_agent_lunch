package de.sjanusch.eventsystem;

public class RegisteredListener {
  private final Listener listen;
  private final Executor executor;
  private final Priority priority;

  public RegisteredListener(final Listener listen, final Executor executor, final Priority priority) {
    this.executor = executor;
    this.listen = listen;
    this.priority = priority;
  }

  public RegisteredListener(final Listener listen, final Executor executor) {
    this(listen, executor, Priority.Normal);
  }

  public Listener getListen() {
    return listen;
  }

  public Executor getExecutor() {
    return executor;
  }

  public void execute(final Event event) throws Exception {
    if (event instanceof Cancelable) {
      if (((Cancelable) event).isCancelled())
        return;
    }
    executor.execute(listen, event);
  }

  public Priority getPriority() {
    return priority;
  }

}
