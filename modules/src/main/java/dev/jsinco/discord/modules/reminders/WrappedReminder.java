package dev.jsinco.discord.modules.reminders;

import dev.jsinco.discord.framework.util.ConfigurationSerializable;
import dev.jsinco.discord.framework.FrameWork;
import dev.jsinco.discord.framework.reflect.InjectStatic;
import dev.jsinco.discord.modules.Util;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.Channel;

import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
@Getter
@Setter
public class WrappedReminder implements ConfigurationSerializable {

    @InjectStatic(from = FrameWork.class)
    private static JDA jda;
    private static final String SPLIT_REGEX = "%@#%";

    private final String identifier;
    private final Channel channel;
    private final String message;
    private final MessageFrequency frequency;
    private final String when;

    private LocalDateTime lastSent;

    public WrappedReminder(Channel channel, String message, MessageFrequency frequency, String when) {
        this.identifier = "SCHEDULED_MESSAGE-#" + ReminderModule.getWRAPPED_REMINDERS().size();
        this.channel = channel;
        this.message = message;
        this.frequency = frequency;
        this.when = when;
        this.lastSent = null;
    }

    public WrappedReminder(String identifier, Channel channel, String message, MessageFrequency frequency, String when) {
        this.identifier = identifier;
        this.channel = channel;
        this.message = message;
        this.frequency = frequency;
        this.when = when;
        this.lastSent = null;
    }

    public boolean shouldSendNow() {
        boolean frequencyTruth;
        if (lastSent == null) {
            frequencyTruth = true;
        } else {
            frequencyTruth = switch (frequency.getUnit()) {
                case NEVER -> false;
                case SEC -> lastSent.plusSeconds(frequency.getNumber()).isBefore(LocalDateTime.now());
                case MIN -> lastSent.plusMinutes(frequency.getNumber()).isBefore(LocalDateTime.now());
                case HR -> lastSent.plusHours(frequency.getNumber()).isBefore(LocalDateTime.now());
                case DAY -> lastSent.plusDays(frequency.getNumber()).isBefore(LocalDateTime.now());
                case WEEK -> lastSent.plusWeeks(frequency.getNumber()).isBefore(LocalDateTime.now());
                case MONTH -> lastSent.plusMonths(frequency.getNumber()).isBefore(LocalDateTime.now());
                //case YEAR -> lastSent.plusYears(frequency.getNumber()).isBefore(LocalDateTime.now());
            };
        }

        return frequencyTruth && Util.parseDateTime(when).isBefore(LocalDateTime.now());
    }

    public String serialize() {
        if (lastSent == null) {
            return String.join(SPLIT_REGEX, identifier, channel.getId(), message, frequency.serialize(), when);
        }
        return String.join(SPLIT_REGEX, identifier, channel.getId(), message, frequency.serialize(), when, lastSent.toString());
    }

    public static WrappedReminder deserialize(String serialized) {
        String[] parts = serialized.split(SPLIT_REGEX);
        if (parts.length < 6) {
            return WrappedReminder.builder()
                    .identifier(parts[0])
                    .channel(jda.getGuildChannelById(parts[1]))
                    .message(parts[2])
                    .frequency(MessageFrequency.deserialize(parts[3]))
                    .when(parts[4])
                    .build();
        }
        return WrappedReminder.builder()
                .identifier(parts[0])
                .channel(jda.getGuildChannelById(parts[1]))
                .message(parts[2])
                .frequency(MessageFrequency.deserialize(parts[3]))
                .when(parts[4])
                .lastSent(LocalDateTime.parse(parts[5]))
                .build();
    }
}
