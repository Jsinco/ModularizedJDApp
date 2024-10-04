package dev.jsinco.discord.events;

import dev.jsinco.discord.FrameWorkLogger;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class EventManager implements EventListener {

    private static EventManager instance;

    private final Map<Class<? extends GenericEvent>, List<Method>> methods = new HashMap<>();

    private EventManager() {
    }

    public static EventManager getInstance() {
        if (instance == null) {
            instance = new EventManager();
        }
        return instance;
    }


    public void registerEvents(ListenerModule listener) {
        for (Method method : listener.getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(EventHandler.class)) {
                continue;
            }

            Class<?>[] params = method.getParameterTypes();
            if (params.length != 1 || !GenericEvent.class.isAssignableFrom(params[0])) {
                continue;
            }

            Class<? extends GenericEvent> event = (Class<? extends GenericEvent>) params[0];
            methods.computeIfAbsent(event, k -> new ArrayList<>()).add(method);
        }
    }

    public void unregisterEvents(ListenerModule listener) {
        for (List<Method> list : methods.values()) {
            list.removeIf(method -> method.getDeclaringClass().equals(listener.getClass()));
        }
    }

    @Override
    public void onEvent(@NotNull GenericEvent event) {
        List<Method> list = methods.get(event.getClass());
        if (list == null) {
            return;
        }

        for (Method method : list) {
            try {
                method.invoke(this, event);
            } catch (Exception e) {
                FrameWorkLogger.error("Error while invoking event " +
                        event.getClass().getSimpleName() + " in " +
                        method.getDeclaringClass().getSimpleName() + " : " + e.getMessage(), e);
            }
        }
    }
}
