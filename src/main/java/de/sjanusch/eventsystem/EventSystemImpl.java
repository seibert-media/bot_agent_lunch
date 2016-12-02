package de.sjanusch.eventsystem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EventSystemImpl implements EventSystem {

  private static final Logger logger = LoggerFactory.getLogger(EventSystemImpl.class);

  public EventSystemImpl() {
  }

  @Override
  public void callEvent(final Event event) {
    final EventList events = event.getEvents();
    final RegisteredListener[] listeners = events.getRegisteredListeners();
    for (final RegisteredListener listen : listeners) {
      try {
        listen.execute(event);
        logger.debug("Execute Event: " + event.getEventName());
      } catch (final Exception e) {
        logger.error("callEvent:" + e.getClass().getName(), e);
      }
    }
  }

  @Override
  public void registerEvents(final Listener l) {
    for (final Map.Entry<Class<? extends Event>, Set<RegisteredListener>> entry : addMuffins(l).entrySet()) {
      try {
        getEventListeners(getRegistrationClass(entry.getKey())).registerAll(entry.getValue());
      } catch (final IllegalAccessException e) {
        logger.error("registerEvents:" + e.getClass().getName(), e);
      }
    }
  }

  private EventList getEventListeners(final Class<? extends Event> type) {
    try {
      final Method method = getRegistrationClass(type).getDeclaredMethod("getEventList");
      method.setAccessible(true);
      return (EventList) method.invoke(null);
    } catch (final Exception e) {
      logger.error("getEventListeners:" + e.getClass().getName(), e);
      return null;
    }
  }

  private Class<? extends Event> getRegistrationClass(final Class<? extends Event> clazz) throws IllegalAccessException {
    try {
      clazz.getDeclaredMethod("getEventList");
      return clazz;
    } catch (final NoSuchMethodException e) {
      if (clazz.getSuperclass() != null && !clazz.getSuperclass().equals(Event.class)
        && Event.class.isAssignableFrom(clazz.getSuperclass())) {
        return getRegistrationClass(clazz.getSuperclass().asSubclass(Event.class));
      } else {
        throw new IllegalAccessException("Unable to find event list for event " + clazz.getName());
      }
    }
  }

  public Map<Class<? extends Event>, Set<RegisteredListener>> addMuffins(final Listener listen) {
    final Map<Class<? extends Event>, Set<RegisteredListener>> ret = new HashMap<>();
    final Method[] methods;
    try {
      methods = listen.getClass().getDeclaredMethods();
    } catch (final NoClassDefFoundError e) {
      return null;
    }
    for (final Method m : methods) {
      if (m.getAnnotation(EventHandler.class) == null)
        continue;
      if (!Event.class.isAssignableFrom(m.getParameterTypes()[0]) || m.getParameterTypes().length > 1)
        continue;
      final Class<? extends Event> eventClass = m.getParameterTypes()[0].asSubclass(Event.class);
      m.setAccessible(true);
      Set<RegisteredListener> events = ret.get(eventClass);
      if (events == null) {
        events = new HashSet<>();
        ret.put(eventClass, events);
      }
      final Executor exe = new Executor() {

        @Override
        public void execute(final Listener listen, final Event e) {
          try {
            if (!eventClass.isAssignableFrom(e.getClass()))
              return;
            m.invoke(listen, e);
          } catch (final Exception e1) {
            e1.printStackTrace();
          }
        }
      };
      events.add(new RegisteredListener(listen, exe, m.getAnnotation(EventHandler.class).priority()));
    }
    return ret;
  }
}
