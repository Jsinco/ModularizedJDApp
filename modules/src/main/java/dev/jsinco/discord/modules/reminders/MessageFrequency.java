package dev.jsinco.discord.modules.reminders;

import dev.jsinco.discord.framework.util.ConfigurationSerializable;
import dev.jsinco.discord.modules.Util;
import lombok.Getter;

@Getter
public class MessageFrequency implements ConfigurationSerializable {

    private final int number;
    private MessageFrequencyUnit unit;

    public MessageFrequency(int number, MessageFrequencyUnit unit) {
        this.number = number;
        this.unit = unit;
        if (unit == null) {
            this.unit = MessageFrequencyUnit.NEVER;
        }
    }

    public MessageFrequency(String fromString) {
        this.number = Integer.parseInt(fromString.replaceAll("[^0-9]", ""));
        this.unit = Util.getEnumByName(MessageFrequencyUnit.class, fromString.replaceAll("[0-9]", ""));
        if (unit == null) {
            this.unit = MessageFrequencyUnit.NEVER;
        }
    }

    public enum MessageFrequencyUnit {
        NEVER,
        SEC,
        MIN,
        HR,
        DAY,
        WEEK,
        MONTH
    }

    @Override
    public String toString() {
        return number + unit.name();
    }

    @Override
    public String serialize() {
        return number + "|" + unit.name();
    }

    public static MessageFrequency deserialize(String serialized) {
        String[] split = serialized.split("\\|");
        return new MessageFrequency(Integer.parseInt(split[0]), Util.getEnumByName(MessageFrequencyUnit.class, split[1]));
    }

}