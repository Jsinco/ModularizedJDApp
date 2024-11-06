package dev.jsinco.discord.modules.moduleimpl.canvas.commands;

import dev.jsinco.discord.framework.commands.CommandModule;
import dev.jsinco.discord.framework.commands.DiscordCommand;
import dev.jsinco.discord.modules.moduleimpl.canvas.DiscordCanvasUser;
import dev.jsinco.discord.modules.moduleimpl.canvas.DiscordCanvasUserManager;
import dev.jsinco.discord.modules.moduleimpl.canvas.Institution;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@DiscordCommand(name = "canvas-unlink", description = "Unlink your canvas account")
public class CanvasUnlinkCommand implements CommandModule {
    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception {
        DiscordCanvasUser user = DiscordCanvasUser.from(event.getUser());
        Institution institution = Institution.UNKNOWN_INSTITUTION;


        if (user != null) {
            institution = user.getInstitution();
            DiscordCanvasUserManager.removeLinkedAccount(user.getDiscordId());
        }

        EmbedBuilder embedBuilder = institution.getEmbed();

        if (user != null) {
            embedBuilder.setTitle("Canvas Account Unlinked");
            embedBuilder.setDescription("You have unlinked your Canvas and Discord accounts.");
        } else {
            embedBuilder.setTitle("No Canvas Account Linked");
            embedBuilder.setDescription("You do not have a linked Canvas account.");
        }
        event.replyEmbeds(embedBuilder.build()).addFiles(institution.getCanvasLogoFileUpload()).queue();
    }
}
