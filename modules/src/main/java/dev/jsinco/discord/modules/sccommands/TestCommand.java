package dev.jsinco.discord.modules.sccommands;

import dev.jsinco.discord.framework.commands.CommandModule;
import dev.jsinco.discord.framework.commands.DiscordCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@DiscordCommand(name = "testcommand", description = "Test command.")
public class TestCommand implements CommandModule {

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.reply("Test").setEphemeral(true).queue();
    }
}
