package dev.jsinco.discord.modules.util;

import dev.jsinco.discord.framework.util.Pair;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.jetbrains.annotations.Nullable;

import java.awt.Color;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public final class Util {

    @Nullable
    public static <E extends Enum<E>> E getEnumByName(Class<E> enumClass, String name) {
        try {
            return Enum.valueOf(enumClass, name.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            return null;
        }
    }

    public static LocalDateTime parseDateTime(String dateTime) {
        String[] parts = dateTime.split("T");
        String[] dateParts = parts[0].split("/");
        String[] timeParts = parts[1].split(":");
        // Our format is MM-dd-yyyyTHH:mm
        return LocalDateTime.of(Integer.parseInt(dateParts[2]), Integer.parseInt(dateParts[0]), Integer.parseInt(dateParts[1]),
                Integer.parseInt(timeParts[0]), Integer.parseInt(timeParts[1]));
    }

    public static LocalDateTime parseDateTime(@Nullable String date, @Nullable String time) {
        // Our format is MM-dd-yyyyTHH:mm

        LocalDate localDate;
        LocalTime localTime;

        if (date != null) {
            String[] dateParts = date.split("/");
            localDate = LocalDate.of(Integer.parseInt(dateParts[2]), Integer.parseInt(dateParts[0]), Integer.parseInt(dateParts[1]));
        } else {
            localDate = LocalDate.now();
        }

        if (time != null) {
            String[] timeParts = time.split(":");
            localTime = LocalTime.of(Integer.parseInt(timeParts[0]), Integer.parseInt(timeParts[1]));
        } else {
            localTime = LocalTime.now();
        }

        return LocalDateTime.of(localDate, localTime);
    }

    public static <E extends Enum<E>> List<Command.Choice> buildChoicesFromEnum(Class<E> enumClass) {
        return List.of(enumClass.getEnumConstants()).stream().map(it -> new Command.Choice(it.name().toLowerCase(), it.name())).toList();
    }

    @Nullable
    public static <T> T getOptionOrNull(OptionMapping optionMapping, OptionType optionType) {
        return getOptionOrNull(optionMapping, optionType, null);
    }

    public static <T> T getOptionOrNull(OptionMapping optionMapping, OptionType optionType, @Nullable T defaultValue) {
        if (optionMapping == null) return defaultValue;

        return switch (optionType) {
            case UNKNOWN, SUB_COMMAND, SUB_COMMAND_GROUP -> defaultValue;
            case STRING -> (T) optionMapping.getAsString();
            case INTEGER -> (T) Integer.valueOf(optionMapping.getAsInt());
            case BOOLEAN -> (T) Boolean.valueOf(optionMapping.getAsBoolean());
            case CHANNEL -> (T) optionMapping.getAsChannel();
            case ROLE -> (T) optionMapping.getAsRole();
            case MENTIONABLE -> (T) optionMapping.getAsMentionable();
            case ATTACHMENT -> (T) optionMapping.getAsAttachment();
            case USER -> {
                try {
                    yield (T) optionMapping.getAsMember();
                } catch (IllegalStateException e) {
                    yield (T) optionMapping.getAsUser();
                }
            }
            case NUMBER -> {
                try {
                    yield (T) Double.valueOf(optionMapping.getAsDouble());
                } catch (IllegalStateException e) {
                    yield (T) Long.valueOf(optionMapping.getAsLong());
                }
            }
        };
    }


    public static Color hex(String hex) {
        return new Color(
                Integer.valueOf(hex.substring(1, 3), 16),
                Integer.valueOf(hex.substring(3, 5), 16),
                Integer.valueOf(hex.substring(5, 7), 16)
        );
    }

    public static Pair<String, String> parseTitle(@Nullable String string) {
        return parseTitle(string, null);
    }

    public static Pair<String, String> parseTitle(@Nullable String string, @Nullable String defaultTitle) {
        if (string == null) {
            return null;
        } else if (!string.contains("title=")) {
            return new Pair<>(getFirstWords(string, 3), string);
        }

        String title = string.substring(string.indexOf("title=\"") + 7, string.indexOf("\"", string.indexOf("title=\"") + 7));
        String body = string.replace("title=\"" + title + "\"", "");
        return new Pair<>(title, body);
    }


    public static String getFirstWords(String text, int amt) {
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
