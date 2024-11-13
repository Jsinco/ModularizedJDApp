package dev.jsinco.discord.modules.canvas.commands.institute;

import dev.jsinco.discord.framework.FrameWork;
import dev.jsinco.discord.framework.commands.CommandModule;
import dev.jsinco.discord.framework.commands.DiscordCommand;
import dev.jsinco.discord.modules.canvas.encapsulation.institute.Institution;
import dev.jsinco.discord.modules.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

@DiscordCommand(name = "canvas-institution-info", description = "Get information about a Canvas institution.", guildOnly = false)
public class CanvasInstitutionInfoCommand implements CommandModule {


    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception {
        Institution institution = Institution.valueOf(Util.getOption(event.getOption("institution"), OptionType.STRING, "nonnull").toUpperCase());
        boolean ephemeral = Util.getOption(event.getOption("ephemeral"), OptionType.BOOLEAN, false);

        EmbedBuilder embed = institution.getEmbed();

        embed.setTitle(institution.getProperName());
        Command cmd = FrameWork.getJda().retrieveCommands().complete().stream().filter(command -> command.getName().equals("canvas-link")).findFirst().orElse(null);


        if (cmd != null) {
            embed.setDescription("Part of this Institution? </canvas-link:" + cmd.getId() + ">");
        }

        embed.addField("Abbreviation", institution.getAbbreviatedName(), true);
        embed.addField("Canvas URL", institution.getUrl(), true);
        embed.addField("Color", "#" + Integer.toHexString(institution.getColor().getRGB()).substring(2), true);

        event.replyEmbeds(embed.build()).addFiles(institution.getCanvasLogoFileUpload()).setEphemeral(ephemeral).queue();
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.STRING, "institution", "The institution you are a part of.", true)
                        .addChoices(Institution.values().stream().filter(institution -> institution != Institution.UNKNOWN_INSTITUTION)
                                .map(institution -> new Command.Choice(institution.getProperName(), institution.getFIELD_NAME())).toList()),
                new OptionData(OptionType.BOOLEAN, "ephemeral", "Should the response be ephemeral? (Non-visible to others)", false)
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
