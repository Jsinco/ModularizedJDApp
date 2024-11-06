package dev.jsinco.discord.modules.moduleimpl.reminders;

import dev.jsinco.discord.modules.util.Util;
import lombok.Getter;

/**
 * Encapsulation for how frequent a wrapped message should be sent.
 * @see ReminderModule
 * @see ReminderDeleteCommand
 * @see WrappedReminder
 * @see WrappedReminderTypeAdapter
 * @author Jonah
 */
@Getter
public class MessageFrequency {

    private int number;
    private MessageFrequencyUnit unit;

    public MessageFrequency(int number, MessageFrequencyUnit unit) {
        this.number = number;
        this.unit = unit;
        if (unit == null) {
            this.unit = MessageFrequencyUnit.NEVER;
        }
    }

    public MessageFrequency(String fromString) {
        try {
            this.number = Integer.parseInt(fromString.replaceAll("[^0-9]", ""));
        } catch (NumberFormatException e) {
            this.number = 0;
        }
        this.unit = Util.getEnumByName(MessageFrequencyUnit.class, fromString.replaceAll("[0-9]", ""));
        if (unit == null) {
            this.unit = MessageFrequencyUnit.NEVER;
        }
    }

    @Override
    public String toString() {
        return number + ";" + unit.name();
    }


    public static MessageFrequency fromString(String string) {
        String[] split = string.split(";");
        return new MessageFrequency(Integer.parseInt(split[0]), Util.getEnumByName(MessageFrequencyUnit.class, split[1]));
    }


    public enum MessageFrequencyUnit {
        NEVER,
        MIN,
        HR,
        DAY,
        WEEK,
        MONTH,
        YEAR
    }
}