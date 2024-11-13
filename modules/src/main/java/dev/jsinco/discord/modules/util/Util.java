package dev.jsinco.discord.modules.util;

import dev.jsinco.discord.framework.FrameWork;
import dev.jsinco.discord.framework.logging.FrameWorkLogger;
import dev.jsinco.discord.framework.util.Pair;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.jetbrains.annotations.Nullable;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;


// TODO: Organize and separate into multiple UTIL classes
public final class Util {

    // Enum

    @Nullable
    public static <E extends Enum<E>> E getEnumByName(Class<E> enumClass, String name) {
        try {
            return Enum.valueOf(enumClass, name.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            return null;
        }
    }

    public static <E extends Enum<E>> List<Command.Choice> buildChoicesFromEnum(Class<E> enumClass) {
        return buildChoicesFromEnum(enumClass, new String[0]);
    }


    public static <E extends Enum<E>> List<Command.Choice> buildChoicesFromEnum(Class<E> enumClass, String... ignore) {
        List<String> ignoreList = List.of(ignore);
        return Stream.of(enumClass.getEnumConstants())
                .map(it -> {
                    if (ignoreList.contains(it.name())) {
                        return null;
                    }
                    return new Command.Choice(it.name().toLowerCase(), it.name());
                })
                .filter(Objects::nonNull)
                .toList();
    }



    // OptionMappings

    @Nullable
    public static <T> T getOption(OptionMapping optionMapping, OptionType optionType) {
        return getOption(optionMapping, optionType, null);
    }

    public static <T> T getOption(OptionMapping optionMapping, OptionType optionType, @Nullable T defaultValue) {
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



    public static File getFile(String name) {
        File file = FrameWork.getDataFolderPath().resolve(name).toFile();
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                FrameWorkLogger.error("Error creating file: " + e.getMessage(), e);
            }
        }
        return file;
    }


    public static boolean isLinkWorking(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(5000); // Set timeout to 5 seconds
            connection.setReadTimeout(5000);
            int responseCode = connection.getResponseCode();
            return (200 <= responseCode && responseCode <= 299);
        } catch (IOException e) {
            return false;
        }
    }
}
