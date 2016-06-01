package de.sjanusch.eventsystem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map.Entry;

public class EventList {

  private volatile RegisteredListener[] events = null;

  private final EnumMap<Priority, ArrayList<RegisteredListener>> muffinbag;

  private static final ArrayList<EventList> mail = new ArrayList<>();

  public EventList() {
    muffinbag = new EnumMap<>(Priority.class);
    for (final Priority o : Priority.values()) {
      muffinbag.put(o, new ArrayList<>());
    }
    synchronized (mail) {
      mail.add(this);
    }
  }

  public synchronized void register(final RegisteredListener listener) {
    if (muffinbag.get(listener.getPriority()).contains(listener))
      throw new IllegalStateException("This listener is already registered!");
    events = null;
    muffinbag.get(listener.getPriority()).add(listener);
  }

  public void registerAll(final Collection<RegisteredListener> listeners) {
    listeners.forEach(this::register);
  }

  public RegisteredListener[] getRegisteredListeners() {
    RegisteredListener[] handlers;
    while ((handlers = this.events) == null)
      bake(); // This prevents fringe cases of returning null
    return handlers;
  }

  public synchronized void bake() {
    if (events != null)
      return; // don't re-bake when still valid
    final List<RegisteredListener> entries = new ArrayList<>();
    for (final Entry<Priority, ArrayList<RegisteredListener>> entry : muffinbag.entrySet()) {
      entries.addAll(entry.getValue());
    }
    events = entries.toArray(new RegisteredListener[entries.size()]);
  }

  public synchronized void unregister(final Listener listener) {
    boolean changed = false;
    for (final List<RegisteredListener> list : muffinbag.values()) {
      for (final ListIterator<RegisteredListener> i = list.listIterator(); i.hasNext();) {
        if (i.next().getListen().equals(listener)) {
          i.remove();
          changed = true;
        }
      }
    }
    if (changed)
      events = null;
  }

}
