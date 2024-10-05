package dev.jsinco.discord;

/**
 * Interface for modules.
 * @see dev.jsinco.discord.events.ListenerModule
 * @see dev.jsinco.discord.commands.CommandModule
 * @since 1.0
 * @author Jonah
 */
public interface AbstractModule {
    void register();
}
