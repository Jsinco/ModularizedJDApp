package club.coding.discord.framework.console.commands;

import club.coding.discord.framework.console.ConsoleCommand;
import club.coding.discord.framework.console.ConsoleCommandManager;
import club.coding.discord.framework.logging.FrameWorkLogger;

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
