package dev.jsinco.discord.modules.moduleimpl.canvas.commands;

import dev.jsinco.discord.framework.commands.DiscordCommand;
import dev.jsinco.discord.modules.moduleimpl.canvas.encapsulation.DiscordCanvasUser;
import dev.jsinco.discord.modules.moduleimpl.canvas.moduleabstract.interfaces.CanvasCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@DiscordCommand(name = "canvas-notifications", description = "Enable or disable Canvas LMS notifications in your DMs", guildOnly = false)
public class CanvasNotificationsCommand implements CanvasCommand {
    @Override
    public void canvasCommand(SlashCommandInteractionEvent event, DiscordCanvasUser canvasUser, boolean ephemeral) {
        EmbedBuilder embedBuilder = canvasUser.getInstitution().getEmbed();

        canvasUser.getUserData().setNotifications(!canvasUser.getUserData().isNotifications());

        embedBuilder.setTitle("Canvas LMS Notifications");
        embedBuilder.setDescription("Canvas LMS Notifications are now " + (canvasUser.getUserData().isNotifications() ? "enabled" : "disabled"));
        event.replyEmbeds(embedBuilder.build()).addFiles(canvasUser.getInstitution().getCanvasLogoFileUpload()).queue();
    }
}
