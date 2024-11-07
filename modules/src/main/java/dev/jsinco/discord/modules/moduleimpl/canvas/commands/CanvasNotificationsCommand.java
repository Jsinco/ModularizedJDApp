package dev.jsinco.discord.modules.moduleimpl.canvas.commands;

import dev.jsinco.discord.framework.commands.DiscordCommand;
import dev.jsinco.discord.modules.moduleimpl.canvas.encapsulation.DiscordCanvasUser;
import dev.jsinco.discord.modules.moduleimpl.canvas.encapsulation.Institution;
import dev.jsinco.discord.modules.moduleimpl.canvas.moduleabstract.interfaces.CanvasCommandModule;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@DiscordCommand(name = "canvas-notifications", description = "Enable or disable Canvas LMS notifications in your DMs", guildOnly = false)
public class CanvasNotificationsCommand implements CanvasCommandModule {
    @Override
    public void canvasCommand(SlashCommandInteractionEvent event, DiscordCanvasUser canvasUser, boolean ephemeral) {
        EmbedBuilder embedBuilder = Institution.UNKNOWN_INSTITUTION.getEmbed();

        if (canvasUser == null) {
            embedBuilder.setTitle("Canvas LMS Notifications");
            embedBuilder.setDescription("You have not linked your Canvas account.");
            event.replyEmbeds(embedBuilder.build()).addFiles(Institution.UNKNOWN_INSTITUTION.getCanvasLogoFileUpload()).queue();
        } else {
            embedBuilder = canvasUser.getInstitution().getEmbed();
            canvasUser.setNotifications(!canvasUser.isNotifications());

            embedBuilder.setTitle("Canvas LMS Notifications");
            embedBuilder.setDescription("Canvas LMS Notifications are now " + (canvasUser.isNotifications() ? "enabled" : "disabled"));
            event.replyEmbeds(embedBuilder.build()).addFiles(canvasUser.getInstitution().getCanvasLogoFileUpload()).queue();
        }
    }
}
