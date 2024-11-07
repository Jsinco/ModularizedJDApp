package dev.jsinco.discord.modules.moduleimpl.canvas.commands.linking;

import dev.jsinco.discord.framework.commands.CommandModule;
import dev.jsinco.discord.framework.commands.DiscordCommand;
import dev.jsinco.discord.modules.moduleimpl.canvas.encapsulation.DiscordCanvasUser;
import dev.jsinco.discord.modules.moduleimpl.canvas.DiscordCanvasUserManager;
import dev.jsinco.discord.modules.moduleimpl.canvas.encapsulation.Institution;
import dev.jsinco.discord.modules.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

@DiscordCommand(name = "canvas-unlink", description = "Unlink your canvas account", guildOnly = false)
public class CanvasUnlinkCommand implements CommandModule {
    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception {
        DiscordCanvasUser user = DiscordCanvasUser.from(event.getUser());
        Institution institution = Institution.UNKNOWN_INSTITUTION;
        boolean ephemeral = Util.getOption(event.getOption("ephemeral"), OptionType.BOOLEAN, false);


        if (user != null) {
            institution = user.getInstitution();
            DiscordCanvasUserManager.getInstance().removeLinkedAccount(user.getDiscordId());
        }

        EmbedBuilder embedBuilder = institution.getEmbed();

        if (user != null) {
            embedBuilder.setTitle("Canvas Account Unlinked");
            embedBuilder.setDescription("You have unlinked your Canvas and Discord accounts.");
        } else {
            embedBuilder.setTitle("No Canvas Account Linked");
            embedBuilder.setDescription("You do not have a linked Canvas account.");
        }
        event.replyEmbeds(embedBuilder.build()).addFiles(institution.getCanvasLogoFileUpload()).setEphemeral(ephemeral).queue();
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.BOOLEAN, "ephemeral", "Should the response be ephemeral? (Non-visible to others)", false)
        );
    }
}
