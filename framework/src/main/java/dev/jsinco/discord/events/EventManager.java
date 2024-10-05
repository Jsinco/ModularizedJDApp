package dev.jsinco.discord.events;

import dev.jsinco.discord.logging.FrameWorkLogger;
import dev.jsinco.discord.FrameWork;
import dev.jsinco.discord.utility.ReflectionUtil;

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
        FrameWorkLogger.info("Registered listener module! (" + listenerModule.getClass().getSimpleName() + ")");
    }

    public static void reflectivelyRegisterEvents() {
        List<Class<?>> listenerClasses = ReflectionUtil.getAllClassesFor(ListenerModule.class);

        int skipped = 0;
        for (Class<?> listenerClass : listenerClasses) {

            try {
                ListenerModule listenerModule = (ListenerModule) listenerClass.getDeclaredConstructor().newInstance();
                listenerModule.register();
            } catch (NoSuchMethodException ignored) {
                // If the class doesn't have a no-args constructor, the developer has to register it manually
                skipped++;
            } catch (Exception e) {
                FrameWorkLogger.error("An error occurred while registering listener module: " + listenerClass.getSimpleName(), e);
            }
        }
        FrameWorkLogger.info("Finished registering listener modules! Skipped " + skipped + " classes.");
    }
}
