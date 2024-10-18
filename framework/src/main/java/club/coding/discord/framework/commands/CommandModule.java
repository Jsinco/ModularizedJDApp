package club.coding.discord.framework.commands;

import club.coding.discord.framework.AbstractModule;
import club.coding.discord.framework.logging.FrameWorkLogger;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Interface for command modules.
 * @see AbstractModule
 * @see CommandManager
 * @see CommandOption
 * @since 1.0
 * @author Jonah
 */
public interface CommandModule extends AbstractModule {

    /**
     * Execute the command.
     * @param event The event that triggered the command.
     */
    void execute(SlashCommandInteractionEvent event);

    /**
     * Get the options for this command.
     * @return The options for this command.
     */
    default List<CommandOption> getOptions() {
        return List.of();
    }

    @Nullable
    default DiscordCommand getCommandInfo() {
        DiscordCommand annotation = getClass().getAnnotation(DiscordCommand.class);
        if (annotation == null) {
            try {
                annotation = getClass().getMethod("execute", SlashCommandInteractionEvent.class).getAnnotation(DiscordCommand.class);
            } catch (NoSuchMethodException e) {
                FrameWorkLogger.error("Failed to get command info for " + getClass().getName(), e);
            }
        }
        return annotation;
    }

    @Override
    default void register() {
        CommandManager.registerCommand(this);
    }
}
