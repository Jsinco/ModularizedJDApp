package dev.jsinco.discord.modules.util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.utils.FileUpload;

import java.util.ArrayList;
import java.util.List;

public final class EmbedUtil {

    public static final int MAX_DESCRIPTION_LENGTH = 4096;


    public static List<MessageEmbed> getLongDescriptionEmbeds(EmbedBuilder embedBuilder, String description) {
        if (description.length() <= MAX_DESCRIPTION_LENGTH) {
            embedBuilder.setDescription(description);
            return List.of(embedBuilder.build());
        } else {
            List<MessageEmbed> embeds = new ArrayList<>();
            for (int i = 0; i < description.length(); i += MAX_DESCRIPTION_LENGTH) {
                String part = description.substring(i, Math.min(description.length(), i + MAX_DESCRIPTION_LENGTH));
                embedBuilder.setDescription(part);
                embeds.add(embedBuilder.build());
            }
            return embeds;
        }

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
