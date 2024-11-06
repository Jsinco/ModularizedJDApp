package dev.jsinco.discord.modules.moduleimpl.canvas.commands;

import dev.jsinco.discord.framework.commands.DiscordCommand;
import dev.jsinco.discord.modules.moduleimpl.canvas.DiscordCanvasUser;
import dev.jsinco.discord.modules.moduleimpl.canvas.moduleabstract.CanvasCommandModule;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@DiscordCommand(name = "canvas-test", description = "Test command for canvas")
public class CanvasTestCommand implements CanvasCommandModule {

    @Override
    public void canvasCommand(SlashCommandInteractionEvent event, DiscordCanvasUser user, boolean ephemeral) throws Exception {

        EmbedBuilder embedBuilder = user.getInstitution().getEmbed();

        embedBuilder.setTitle("Canvas Test Command");
        embedBuilder.setDescription("This is a test command for the Canvas API");
        embedBuilder.setImage(user.getInstitution().getCanvasLogoUrl());

        event.replyEmbeds(embedBuilder.build()).setEphemeral(ephemeral).addFiles(user.getInstitution().getCanvasLogoFileUpload()).queue();
    }
}
