package dev.jsinco.discord.modules;

import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

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
        String[] parts = dateTime.split("\\|");
        String[] dateParts = parts[0].split("-");
        String[] timeParts = parts[1].split(":");
        // Our format is MM-dd-yyyy|HH:mm
        return LocalDateTime.of(Integer.parseInt(dateParts[2]), Integer.parseInt(dateParts[0]), Integer.parseInt(dateParts[1]),
                Integer.parseInt(timeParts[0]), Integer.parseInt(timeParts[1]));
    }
}
