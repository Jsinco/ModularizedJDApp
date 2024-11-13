package dev.jsinco.discord.modules.util;

import dev.jsinco.discord.framework.util.Pair;
import org.jetbrains.annotations.Nullable;

import java.awt.Color;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public final class StringUtil {

    // String utils

    public static String convertToAmPm(String time24) {
        // trim seconds
        if (countCharacterInstances(time24, ':') > 1) {
            time24 = time24.substring(0, time24.lastIndexOf(":"));
        }
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("hh:mm a");

        LocalTime time = LocalTime.parse(time24, inputFormatter);
        return time.format(outputFormatter).replaceFirst("^0+(?!$)", "");
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

    public static Color hex(String hex) {
        return new Color(
                Integer.valueOf(hex.substring(1, 3), 16),
                Integer.valueOf(hex.substring(3, 5), 16),
                Integer.valueOf(hex.substring(5, 7), 16)
        );
    }

    @Nullable
    public static String getFromEnvironment(String key) {
        String str = System.getProperty(key);
        if (str == null) {
            str = System.getenv(key);
        }
        return str;
    }

    public static String cutOffString(String input, int maxLength) {
        if (input == null) {
            return null;
        }
        return input.length() <= maxLength ? input : input.substring(0, maxLength);
    }


    public static int countCharacterInstances(String input, char character) {
        if (input == null) {
            return 0;
        }
        int count = 0;
        for (char c : input.toCharArray()) {
            if (c == character) {
                count++;
            }
        }
        return count;
    }


    public static String capitalizeAfterSpace(String input) {
        StringBuilder result = new StringBuilder(input.length());
        boolean capitalizeNext = false;

        for (char c : input.toCharArray()) {
            if (capitalizeNext && Character.isLetter(c)) {
                result.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else {
                result.append(c);
            }
            if (c == ' ') {
                capitalizeNext = true;
            }
        }

        return result.toString();
    }
}
