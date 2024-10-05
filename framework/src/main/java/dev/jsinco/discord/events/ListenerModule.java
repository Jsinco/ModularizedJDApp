package dev.jsinco.discord.events;

import dev.jsinco.discord.AbstractModule;

/**
 * Interface for modules that listen for events.
 * @see AbstractModule
 * @see EventManager
 * @author Jonah
 * @since 1.0
 */
public interface ListenerModule extends AbstractModule {
    @Override
    default void register() {
        EventManager.registerEvents(this);
    }
}
