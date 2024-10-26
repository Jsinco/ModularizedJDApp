package dev.jsinco.discord.framework.events;

import dev.jsinco.discord.framework.reflect.ReflectionUtil;
import dev.jsinco.discord.framework.logging.FrameWorkLogger;
import dev.jsinco.discord.framework.FrameWork;

import java.util.List;

/**
 * Manages the registration of listener modules.
 * Classes which don't have no-args constructors must handle their registration manually.
 *
 * @since 1.0
 * @author Jonah
 * @see ListenerModule
 */
public final class EventManager {

    /**
     * Registers a listener module with the JDA instance.
     * @param listenerModule The listener module to register.
     */
    public static void registerEvents(ListenerModule listenerModule) {
        FrameWork.getDiscordApp().addEventListener(listenerModule);
    }

}
