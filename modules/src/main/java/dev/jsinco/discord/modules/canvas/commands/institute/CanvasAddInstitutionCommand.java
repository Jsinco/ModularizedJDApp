package dev.jsinco.discord.modules.canvas.commands.institute;

import dev.jsinco.discord.framework.commands.CommandModule;
import dev.jsinco.discord.framework.commands.DiscordCommand;
import dev.jsinco.discord.modules.canvas.encapsulation.institute.Institution;
import dev.jsinco.discord.modules.util.CanvasUtil;
import dev.jsinco.discord.modules.util.StringUtil;
import dev.jsinco.discord.modules.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

@DiscordCommand(name = "canvas-add-institution", description = "Add a custom institution.", guildOnly = false)
public class CanvasAddInstitutionCommand implements CommandModule {


    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception {
        event.deferReply().queue();

        String canvasUrl = Util.getOption(event.getOption("canvas-url"), OptionType.STRING, "Should not be null");
        boolean ephemeral = Util.getOption(event.getOption("ephemeral"), OptionType.BOOLEAN, false);

        if (!CanvasUtil.isValidCanvasURL(canvasUrl) || !Util.isLinkWorking(canvasUrl)) {
            EmbedBuilder embedBuilder = Institution.UNKNOWN_INSTITUTION.getEmbed();
            embedBuilder.setTitle("Canvas URL Not Reachable");
            embedBuilder.setDescription("The Canvas URL for the institution you are trying to add is unreachable. (" + canvasUrl + ")");
            embedBuilder.setFooter("Did you use http:// instead of https://?");
            event.getHook().sendMessageEmbeds(embedBuilder.build()).addFiles(Institution.UNKNOWN_INSTITUTION.getCanvasLogoFileUpload()).setEphemeral(ephemeral).queue();
            return;
        }


        String properName = StringUtil.capitalizeAfterSpace(Util.getOption(event.getOption("proper-name"), OptionType.STRING, "Should not be null").strip());
        String abbreviatedName = Util.getOption(event.getOption("abbreviated-name"), OptionType.STRING, "Should not be null").toUpperCase();
        String color = Util.getOption(event.getOption("color"), OptionType.STRING, "Should not be null");
        String FIELD_NAME = properName.replace(" ", "_").toUpperCase();

        Institution institution = new Institution(canvasUrl, properName, abbreviatedName, color);
        institution.setFIELD_NAME(FIELD_NAME);
        institution.setCustom(true);

        if (Institution.VALUES.containsKey(FIELD_NAME)) {
            EmbedBuilder embedBuilder = Institution.UNKNOWN_INSTITUTION.getEmbed();
            embedBuilder.setTitle("Institution Already Exists");
            embedBuilder.setDescription("The institution you are trying to add already exists. (" + FIELD_NAME + ")");
            event.getHook().sendMessageEmbeds(embedBuilder.build()).addFiles(Institution.UNKNOWN_INSTITUTION.getCanvasLogoFileUpload()).setEphemeral(ephemeral).queue();
            return;
        }
        Institution.VALUES.put(FIELD_NAME, institution);
        Institution.saveCustomInstitutions();

        event.getHook().sendMessageEmbeds(institution.getEmbed()
                .setTitle("Institution Added")
                .addField("Proper Name", properName, true)
                .addField("Abbreviated Name", abbreviatedName, true)
                .addField("Canvas URL", canvasUrl, true)
                .addField("Color", color, true)
                .build()
        ).addFiles(institution.getCanvasLogoFileUpload()).setEphemeral(ephemeral).queue();
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.STRING, "proper-name", "The proper name of the institution. (Full institution name.)", true),
                new OptionData(OptionType.STRING, "canvas-url", "The Canvas URL of the institution.", true),
                new OptionData(OptionType.STRING, "abbreviated-name", "The abbreviated name of the institution.", true),
                new OptionData(OptionType.STRING, "color", "The color of the institution.", true),
                new OptionData(OptionType.BOOLEAN, "ephemeral", "Should the response be ephemeral? (Non-visible to others)", false)
        );
    }
}
