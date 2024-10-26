package dev.jsinco.discord.framework.util;

import dev.jsinco.discord.framework.commands.CommandModule;
import dev.jsinco.discord.framework.events.ListenerModule;

/**
 * Interface for implementing both CommandModule and ListenerModule
 * @see CommandModule
 * @see ListenerModule
 * @since 1.0
 * @author Jonah
 */
public interface Module extends ListenerModule, CommandModule {

    // FIXME: Maybe instead of this, we should have CommandModule extend ListenerModule??
    @Override
    default void register() {
        ListenerModule.super.register();
        CommandModule.super.register();
    }
}
