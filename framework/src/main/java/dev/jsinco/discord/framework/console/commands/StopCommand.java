package dev.jsinco.discord.framework.console.commands;

import dev.jsinco.discord.framework.console.ConsoleCommand;
import dev.jsinco.discord.framework.FrameWork;
import dev.jsinco.discord.framework.logging.FrameWorkLogger;

public class StopCommand implements ConsoleCommand {
    @Override
    public String name() {
        return "stop";
    }

    @Override
    public void execute(String[] args) {
        FrameWorkLogger.info("Stopping!");
        FrameWork.getJda().shutdownNow();
        System.exit(0);
    }
}
