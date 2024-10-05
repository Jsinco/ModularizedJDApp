package dev.jsinco.discord.commands;

import net.dv8tion.jda.api.interactions.commands.OptionType;

/**
 * Encapsulation for Discord command options.
 * @since 1.0
 * @author Jonah
 * @see CommandModule
 * @see DiscordCommand
 * @see CommandManager
 */
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

    public String getName() {
        return name;
    }

    public OptionType getOptionType() {
        return optionType;
    }

    public String getDescription() {
        return description;
    }

    public boolean isRequired() {
        return required;
    }
}
