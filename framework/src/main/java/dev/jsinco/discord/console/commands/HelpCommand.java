package dev.jsinco.discord.console.commands;

import dev.jsinco.discord.console.ConsoleCommand;
import dev.jsinco.discord.console.ConsoleCommandManager;
import dev.jsinco.discord.logging.FrameWorkLogger;

public class HelpCommand implements ConsoleCommand {
    @Override
    public String name() {
        return "help";
    }

    @Override
    public void execute(String[] args) {
        FrameWorkLogger.info("Available commands:");
        for (String command : ConsoleCommandManager.getInstance().getCommands().keySet()) {
            FrameWorkLogger.info("- " + command);
        }
    }
}
