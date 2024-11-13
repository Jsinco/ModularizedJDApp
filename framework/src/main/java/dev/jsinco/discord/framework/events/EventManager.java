package dev.jsinco.discord.framework.events;

import dev.jsinco.discord.framework.FrameWork;
import dev.jsinco.discord.framework.logging.FrameWorkLogger;
import lombok.Getter;

import java.util.*;

/**
 * Manages the registration of listener modules.
 * Classes which don't have no-args constructors must handle their registration manually.
 *
 * @since 1.0
 * @author Jonah
 * @see ListenerModule
 */
public final class EventManager {

    @Getter
    private static final Map<ListenerModuleState, Set<ListenerModule>> listeners = Map.of(
            ListenerModuleState.ACTIVE, new HashSet<>(),
            ListenerModuleState.INACTIVE, new HashSet<>()
    );

    /**
     * Registers a listener module with the JDA instance.
     * @param listenerModule The listener module to register.
     */
    public static void registerEvents(ListenerModule listenerModule) {
        FrameWork.getJda().addEventListener(listenerModule);
        listeners.get(ListenerModuleState.ACTIVE).add(listenerModule);
        listeners.get(ListenerModuleState.INACTIVE).remove(listenerModule);
    }

    public static void unregisterEvents(ListenerModule listenerModule) {
        FrameWork.getJda().removeEventListener(listenerModule);
        listeners.get(ListenerModuleState.ACTIVE).remove(listenerModule);
        listeners.get(ListenerModuleState.INACTIVE).add(listenerModule);
    }

    public static void registerInactiveEvent(ListenerModule listenerModule) {
        listeners.get(ListenerModuleState.INACTIVE).add(listenerModule);
    }


    public static Set<ListenerModule> getListeners(ListenerModuleState state) {
        return listeners.get(state);
    }
}
