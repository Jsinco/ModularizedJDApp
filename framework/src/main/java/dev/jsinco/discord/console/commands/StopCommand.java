package dev.jsinco.discord.console.commands;

import dev.jsinco.discord.FrameWork;
import dev.jsinco.discord.console.ConsoleCommand;
import dev.jsinco.discord.logging.FrameWorkLogger;

public class StopCommand implements ConsoleCommand {
    @Override
    public String name() {
        return "stop";
    }

    @Override
    public void execute(String[] args) {
        FrameWorkLogger.info("Stopping!");
        FrameWork.getDiscordApp().shutdownNow();
        System.exit(0);
    }
}
