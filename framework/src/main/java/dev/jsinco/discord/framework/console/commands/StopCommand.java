package dev.jsinco.discord.framework.console.commands;

import dev.jsinco.discord.framework.console.ConsoleCommand;
import dev.jsinco.discord.framework.FrameWork;
import dev.jsinco.discord.framework.logging.FrameWorkLogger;
import dev.jsinco.discord.framework.scheduling.ScheduleManager;
import dev.jsinco.discord.framework.scheduling.Tickable;

public class StopCommand implements ConsoleCommand {
    @Override
    public String name() {
        return "stop";
    }

    @Override
    public void execute(String[] args) {
        FrameWorkLogger.info("Stopping!");
        for (Tickable tickable : ScheduleManager.getInstance().getScheduledTasks()) {
            tickable.run();
        }
        FrameWork.getJda().shutdownNow();
        System.exit(0);
    }
}
