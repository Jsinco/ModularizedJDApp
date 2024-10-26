package dev.jsinco.discord.framework.console.commands;

import dev.jsinco.discord.framework.console.ConsoleCommand;
import dev.jsinco.discord.framework.console.ConsoleCommandManager;
import dev.jsinco.discord.framework.logging.FrameWorkLogger;

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
