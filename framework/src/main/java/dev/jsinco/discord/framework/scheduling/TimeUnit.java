package dev.jsinco.discord.framework.scheduling;

public enum TimeUnit {

    MILLISECONDS(0L),
    SECONDS(1000L),
    MINUTES(60000L),
    HOURS(3600000L),
    DAYS(86400000L),
    WEEKS(604800000L),
    MONTHS(2628000000L),
    YEARS(31536000000L);


    private final long multiplier;

    TimeUnit(long multiplier) {
        this.multiplier = multiplier;
    }

    public long toMillis(long amount) {
        return amount * multiplier;
    }
}
