package dev.jsinco.discord.framework.console;

public interface ConsoleCommand {
    String name();
    void execute(String[] args);
}
