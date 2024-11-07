package dev.jsinco.discord.modules.util;

import dev.jsinco.discord.modules.moduleimpl.canvas.encapsulation.DiscordCanvasUser;
import edu.ksu.canvas.model.DiscussionTopic;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.utils.FileUpload;

public final class EmbedUtil {

    public static final int MAX_DESCRIPTION_LENGTH = 4096;

    // Sends the embed with the max description and then appends a button to view the rest.
    public static void sendDiscussionEmbed(DiscordCanvasUser user, MessageChannel channel, DiscussionTopic topic) {

    }

    public static void sendLongDescriptionEmbed(EmbedBuilder embedBuilder, String description, MessageChannel channel, FileUpload... files) {
        int maxLength = 4096;
        for (int i = 0; i < description.length(); i += maxLength) {
            String part = description.substring(i, Math.min(description.length(), i + maxLength));
            embedBuilder.setDescription(part);
            channel.sendMessageEmbeds(embedBuilder.build()).addFiles(files).queue();
        }
    }

    public static void sendLongDescriptionEmbed(EmbedBuilder embedBuilder, String description, MessageChannel channel) {
        String[] parts = description.split("(?<=\\G.{4096})");
        for (String part : parts) {
            embedBuilder.setDescription(part);
            channel.sendMessageEmbeds(embedBuilder.build()).queue();
        }
    }
}
