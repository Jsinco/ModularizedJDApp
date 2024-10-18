package club.coding.discord.framework;

import club.coding.discord.framework.commands.CommandModule;
import club.coding.discord.framework.events.ListenerModule;

/**
 * Interface for modules.
 * @see ListenerModule
 * @see CommandModule
 * @since 1.0
 * @author Jonah
 */
public interface AbstractModule {
    void register();
}
