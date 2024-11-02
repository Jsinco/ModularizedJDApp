package dev.jsinco.discord.modules.embeds;

import dev.jsinco.discord.framework.commands.CommandModule;
import dev.jsinco.discord.framework.commands.DiscordCommand;
import dev.jsinco.discord.framework.util.Pair;
import dev.jsinco.discord.modules.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@DiscordCommand(name = "embed", description = "Create an embed", permission = Permission.MANAGE_CHANNEL)
public class EmbedCreatorCommand implements CommandModule {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Pair<String, String> body = this.parseTitle(Util.getOptionOrNull(event.getOption("body"), OptionType.STRING, "Should not be null"));
        String color = Util.getOptionOrNull(event.getOption("color"), OptionType.STRING, "#000000");
        String thumbnail = Util.getOptionOrNull(event.getOption("thumbnail"), OptionType.STRING);

        String[] fields = new String[] {Util.getOptionOrNull(event.getOption("field_1"), OptionType.STRING),
                Util.getOptionOrNull(event.getOption("field_2"), OptionType.STRING),
                Util.getOptionOrNull(event.getOption("field_3"), OptionType.STRING)};

        String image = Util.getOptionOrNull(event.getOption("image"), OptionType.STRING);
        String footer = Util.getOptionOrNull(event.getOption("footer"), OptionType.STRING);
        TextChannel channel = Util.getOptionOrNull(event.getOption("channel"), OptionType.CHANNEL, event.getChannel()).asTextChannel();

        EmbedBuilder embedBuilder = new EmbedBuilder().setAuthor(event.getUser().getName(), null, event.getUser().getAvatarUrl());

        embedBuilder.setTitle(body.first());
        embedBuilder.setDescription(body.second());
        embedBuilder.setColor(Util.hex(color));


        for (String field : fields) {
            if (field == null) {
                continue;
            }

            Pair<String, String> fieldPair = this.parseTitle(field);
            embedBuilder.addField(fieldPair.first(), fieldPair.second(), true);
        }

        if (thumbnail != null) {
            embedBuilder.setThumbnail(thumbnail);
        }
        if (image != null) {
            embedBuilder.setImage(image);
        }
        if (footer != null) {
            embedBuilder.setFooter(footer);
        }

        channel.sendMessageEmbeds(embedBuilder.build()).queue();
        event.reply("Embed sent!").setEphemeral(true).queue();
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.STRING, "body", "The body of the embed. Set a title with title=\"EXAMPLE\"", true),
                new OptionData(OptionType.STRING, "color", "The color of the embed. This should be a hex code (#fffff)", false),
                new OptionData(OptionType.STRING, "thumbnail", "The thumbnail of the embed. This should be a URL.", false),
                new OptionData(OptionType.STRING, "field_1", "The first field of the embed. Set a title with title=\"EXAMPLE\"", false),
                new OptionData(OptionType.STRING, "field_2", "The second field of the embed. Set a title with title=\"EXAMPLE\"", false),
                new OptionData(OptionType.STRING, "field_3", "The third field of the embed. Set a title with title=\"EXAMPLE\"", false),
                new OptionData(OptionType.STRING, "image", "The image of the embed. This should be a URL.", false),
                new OptionData(OptionType.STRING, "footer", "The footer of the embed.", false),
                new OptionData(OptionType.CHANNEL, "channel", "The channel to send the embed in.", false)
        );
    }

    private Pair<String, String> parseTitle(@Nullable String string) {
        if (string == null) {
            return null;
        } else if (!string.contains("title=")) {
            return new Pair<>(this.getFirstWords(string, 3), string);
        }

        String title = string.substring(string.indexOf("title=\"") + 7, string.indexOf("\"", string.indexOf("title=\"") + 7));
        String body = string.replace("title=\"" + title + "\"", "");
        return new Pair<>(title, body);
    }


    private String getFirstWords(String text, int amt) {
        String[] words = text.split("\\s+");
        StringBuilder firstThree = new StringBuilder();
        for (int i = 0; i < Math.min(amt, words.length); i++) {
            if (i > 0) {
                firstThree.append(" ");
            }
            firstThree.append(words[i]);
        }
        return firstThree.toString();
    }
}
