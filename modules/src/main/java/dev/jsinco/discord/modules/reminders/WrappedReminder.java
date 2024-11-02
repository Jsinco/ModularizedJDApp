package dev.jsinco.discord.modules.reminders;

import dev.jsinco.discord.framework.serdes.TypeAdapter;
import dev.jsinco.discord.framework.FrameWork;
import dev.jsinco.discord.framework.reflect.InjectStatic;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.awt.Color;
import java.time.LocalDateTime;

@TypeAdapter(WrappedReminderTypeAdapter.class) // Custom TypeAdapter for GSON
@AllArgsConstructor @Builder @Getter @Setter // lombok stuff
public class WrappedReminder {

    @InjectStatic(value = FrameWork.class)
    private static JDA jda;


    private final String identifier;
    private final TextChannel channel;
    private final String message;
    private final MessageFrequency frequency;
    private final LocalDateTime when;

    private LocalDateTime lastSent;


    public boolean isValid() {
        return (frequency.getUnit() != MessageFrequency.MessageFrequencyUnit.NEVER || lastSent == null) && frequency.getNumber() > 0;
    }

    public Message send() {
        return this.send(this.channel, false);
    }

    public Message send(TextChannel channel, boolean preview) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        String message = this.message;
        String title = "Reminder";
        if (message.contains("TITLE=")) {
            title = message.substring(message.indexOf("TITLE=") + 6, message.indexOf(",END"));
            message = message.replace("TITLE=" + title, "").replace(",END", "");
        }

        if (preview) {
            title = "Preview: " + title;
        } else {
            lastSent = LocalDateTime.now();
        }

        embedBuilder.setTitle("**" + title + "**");
        embedBuilder.setDescription(message);
        embedBuilder.setFooter("Id: " + identifier);
        embedBuilder.setColor(Color.PINK);
        embedBuilder.setThumbnail(channel.getGuild().getIconUrl());
        return channel.sendMessageEmbeds(embedBuilder.build()).complete();
    }


    public boolean shouldSendNow() {
        boolean frequencyTruth;
        if (lastSent == null) {
            frequencyTruth = true;
        } else {
            frequencyTruth = switch (frequency.getUnit()) {
                case NEVER -> false;
                //case SEC -> lastSent.plusSeconds(frequency.getNumber()).isBefore(LocalDateTime.now());
                case MIN -> lastSent.plusMinutes(frequency.getNumber()).isBefore(LocalDateTime.now());
                case HR -> lastSent.plusHours(frequency.getNumber()).isBefore(LocalDateTime.now());
                case DAY -> lastSent.plusDays(frequency.getNumber()).isBefore(LocalDateTime.now());
                case WEEK -> lastSent.plusWeeks(frequency.getNumber()).isBefore(LocalDateTime.now());
                case MONTH -> lastSent.plusMonths(frequency.getNumber()).isBefore(LocalDateTime.now());
                case YEAR -> lastSent.plusYears(frequency.getNumber()).isBefore(LocalDateTime.now());
            };
        }

        return frequencyTruth && LocalDateTime.now().isAfter(when);
    }

}
