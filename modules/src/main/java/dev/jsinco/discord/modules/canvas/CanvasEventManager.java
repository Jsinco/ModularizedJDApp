package dev.jsinco.discord.modules.canvas;

import dev.jsinco.discord.framework.logging.FrameWorkLogger;
import dev.jsinco.discord.framework.reflect.ReflectionUtil;
import dev.jsinco.discord.framework.scheduling.ScheduleManager;
import dev.jsinco.discord.framework.scheduling.TimeUnit;
import dev.jsinco.discord.framework.util.AutoInstantiated;
import dev.jsinco.discord.modules.canvas.moduleabstract.impl.CanvasAssignmentDueEventImpl;
import dev.jsinco.discord.modules.canvas.moduleabstract.impl.CanvasCourseAnnouncementEventImpl;
import dev.jsinco.discord.modules.canvas.moduleabstract.interfaces.CanvasEventMethod;
import dev.jsinco.discord.modules.canvas.moduleabstract.interfaces.CanvasEvent;
import edu.ksu.canvas.interfaces.CanvasReader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class CanvasEventManager implements AutoInstantiated {

    private static final Map<Method, CanvasEvent> dispatcherMap = new HashMap<>();
    private static final ScheduleManager scheduleManager = ScheduleManager.getInstance();

    @Override
    public void onInstantiation() {
        registerEventsReflectively();

        scheduleManager.schedule(new CanvasCourseAnnouncementEventImpl(TimeUnit.MINUTES, 0, 10));
        scheduleManager.schedule(new CanvasAssignmentDueEventImpl(TimeUnit.HOURS, 0, 8));
    }


    public static void registerEventsReflectively() {
        Set<Class<?>> classes = ReflectionUtil.getAllClassesFor(CanvasEvent.class);

        for (Class<?> clazz : classes) {
            if (clazz.isInterface()) {
                continue;
            }

            CanvasEvent insance = null;

            for (Method method : clazz.getDeclaredMethods()) {
                if (!method.isAnnotationPresent(CanvasEventMethod.class)) {
                    continue;
                }
                try {
                    if (insance == null) {
                        insance = (CanvasEvent) clazz.getConstructor().newInstance();
                    }
                    dispatcherMap.put(method, insance);
                } catch (InstantiationException | IllegalAccessException e) {
                    FrameWorkLogger.error("Error registering CanvasLMSEvent!", e);
                } catch (InvocationTargetException | NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static Map<Method, CanvasEvent> getEvents(Class<? extends CanvasReader> reader) {
        Map<Method, CanvasEvent> events = new HashMap<>();
        for (var entry : dispatcherMap.entrySet()) {
            if (entry.getKey().getAnnotation(CanvasEventMethod.class).value().equals(reader)) {
                events.put(entry.getKey(), entry.getValue());
            }
        }
        return events;
    }


    public static void dispatchEvent(Class<? extends CanvasReader> reader, Object... arguments) {
        for (var mapEntry : getEvents(reader).entrySet()) {
            Method method = mapEntry.getKey();
            CanvasEvent eventInstance = mapEntry.getValue();
            try {
                method.invoke(eventInstance, arguments);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                FrameWorkLogger.error("Error dispatching CanvasLMSEvent!", e);
            }
        }
    }


}
