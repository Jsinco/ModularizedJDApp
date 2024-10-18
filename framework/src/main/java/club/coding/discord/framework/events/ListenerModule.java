package club.coding.discord.framework.events;

import club.coding.discord.framework.AbstractModule;

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
