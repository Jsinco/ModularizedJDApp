package dev.jsinco.discord.framework.commands;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import net.dv8tion.jda.api.interactions.commands.OptionType;

/**
 * Encapsulation for Discord command options.
 * @since 1.0
 * @author Jonah
 * @see CommandModule
 * @see DiscordCommand
 * @see CommandManager
 */
@Getter
@Builder
public class CommandOption { // Leaving as a data class rather than a record.

    private final String name;
    private final OptionType optionType;
    private final String description;
    private final boolean required;

    public CommandOption(String name, OptionType optionType, String description, boolean required) {
        this.name = name;
        this.optionType = optionType;
        this.description = description;
        this.required = required;
    }

}
