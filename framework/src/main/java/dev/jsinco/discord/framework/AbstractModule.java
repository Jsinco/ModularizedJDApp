package dev.jsinco.discord.framework;

import dev.jsinco.discord.framework.commands.CommandModule;
import dev.jsinco.discord.framework.events.ListenerModule;

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
