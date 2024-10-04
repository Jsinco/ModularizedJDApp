package dev.jsinco.discord.events;

public enum EventPriority {

    NORMAL(0),
    HIGH(1);

    private final int position;

    EventPriority(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }
}
