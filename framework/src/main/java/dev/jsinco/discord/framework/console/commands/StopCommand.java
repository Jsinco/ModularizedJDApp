package dev.jsinco.discord.framework.console.commands;

import dev.jsinco.discord.framework.console.ConsoleCommand;
import dev.jsinco.discord.framework.FrameWork;
import dev.jsinco.discord.framework.logging.FrameWorkLogger;
import dev.jsinco.discord.framework.shutdown.ShutdownManager;

public class StopCommand implements ConsoleCommand {
    @Override
    public String name() {
        return "stop";
    }

    @Override
    public void execute(String[] args) {
        FrameWorkLogger.info("Stopping!");
        ShutdownManager.shutDownClasses();
        FrameWork.getJda().shutdownNow();
        System.exit(0);
    }
}
