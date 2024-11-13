package dev.jsinco.discord.modules.canvas.commands.linking;

import dev.jsinco.discord.framework.commands.CommandModule;
import dev.jsinco.discord.framework.commands.DiscordCommand;
import dev.jsinco.discord.modules.canvas.DiscordCanvasUserManager;
import dev.jsinco.discord.modules.canvas.encapsulation.institute.Institution;
import dev.jsinco.discord.modules.util.Util;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

@DiscordCommand(name = "canvas-link", description = "Link your Canvas account. Tokens are encrypted with AES encryption and stored locally.", guildOnly = false)
public class CanvasLinkCommand implements CommandModule {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (event.isFromGuild()) {
            event.reply("This command can only be used in DMs.").setEphemeral(true).queue();
            return;
        }
        String token = Util.getOption(event.getOption("token"), OptionType.STRING, "Should not be null");
        Institution institution = Institution.valueOf(Util.getOption(event.getOption("institution"), OptionType.STRING, "Should not be null"));



        DiscordCanvasUserManager.getInstance().createLinkedAccount(event.getUser().getId(), token, institution);
        event.replyEmbeds(institution.getEmbed()
                .setTitle("Canvas Account Linked")
                .addField("Institution", institution.getProperName(), true)
                .addField("Discord User", event.getUser().getName(), true)
                .setFooter("You may only have one Canvas account linked at a time. Running this command again will overwrite your current link.")
                .build()
        ).addFiles(institution.getCanvasLogoFileUpload()).setEphemeral(true).queue();
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.STRING, "token", "The Canvas LMS token to link your Discord account with.", true),
                new OptionData(OptionType.STRING, "institution", "The institution you are a part of.", true)
                        .addChoices(Institution.values().stream().filter(institution -> institution != Institution.UNKNOWN_INSTITUTION)
                                .map(institution -> new Command.Choice(institution.getProperName(), institution.getFIELD_NAME())).toList())
        );
    }

    private int institutionValuesHash = Institution.VALUES.hashCode();
    @Override
    public boolean persistRegistration() {
        if (institutionValuesHash != Institution.VALUES.hashCode()) {
            institutionValuesHash = Institution.VALUES.hashCode();
            return true;
        }
        return false;
    }
}
