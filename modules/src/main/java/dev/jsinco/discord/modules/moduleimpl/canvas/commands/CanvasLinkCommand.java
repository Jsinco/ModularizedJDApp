package dev.jsinco.discord.modules.moduleimpl.canvas.commands;

import dev.jsinco.discord.framework.commands.CommandModule;
import dev.jsinco.discord.framework.commands.DiscordCommand;
import dev.jsinco.discord.modules.moduleimpl.canvas.DiscordCanvasUserManager;
import dev.jsinco.discord.modules.moduleimpl.canvas.Institution;
import dev.jsinco.discord.modules.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

@DiscordCommand(name = "canvas-link", description = "Link your Discord account with your Canvas account.")
public class CanvasLinkCommand implements CommandModule {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String token = Util.getOption(event.getOption("token"), OptionType.STRING, "Should not be null");
        Institution institution = Institution.valueOf(Util.getOption(event.getOption("institution"), OptionType.STRING, "Should not be null"));

        DiscordCanvasUserManager.createLinkedAccount(event.getUser().getId(), token, institution);
        event.replyEmbeds(institution.getEmbed()
                .setTitle("**Canvas Account Linked**")
                .addField("Institution", institution.getProperName(), true)
                .addField("Discord User", event.getUser().getName(), true)
                .build()
        ).setEphemeral(true).queue();
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.STRING, "token", "The Canvas LMS token to link your Discord account with.", true),
                new OptionData(OptionType.STRING, "institution", "The institution you are a part of.", true).addChoices(Util.buildChoicesFromEnum(Institution.class, "UNKNOWN_INSTITUTION"))
        );
    }
}
