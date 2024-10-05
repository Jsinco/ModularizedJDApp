package dev.jsinco.discord.sccommands;

import dev.jsinco.discord.commands.CommandModule;
import dev.jsinco.discord.commands.DiscordCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@DiscordCommand(name = "credits", description = "Show the credits for this bot.")
public class CreditsCommand implements CommandModule {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
//        ProjectInfo projectInfo = new ProjectInfo("project.yml");
//
//        StringBuilder builder = new StringBuilder("**ModularizedJDApp v" + projectInfo.getVersion() + "**");
//
//        for (var author : projectInfo.getAuthors().entrySet()) {
//            builder.append("\n").append("[").append(author.getKey()).append("]")
//                    .append("(").append(author.getValue()).append(")");
//        }
//
//        event.reply(builder.toString()).queue();
        event.reply("This is a test").setEphemeral(true).queue();
    }
}
