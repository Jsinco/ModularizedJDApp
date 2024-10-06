package dev.jsinco.discord.console;

public interface ConsoleCommand {
    String name();
    void execute(String[] args);
}
