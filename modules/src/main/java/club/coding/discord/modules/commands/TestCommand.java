package club.coding.discord.modules.commands;

import club.coding.discord.framework.commands.CommandModule;
import club.coding.discord.framework.commands.DiscordCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@DiscordCommand(name = "testcommand", description = "Test command.")
public class TestCommand implements CommandModule {

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.reply("Test").setEphemeral(true).queue();
    }
}
