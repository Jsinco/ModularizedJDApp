package club.coding.discord.framework.console.commands;

import club.coding.discord.framework.console.ConsoleCommand;
import club.coding.discord.framework.FrameWork;
import club.coding.discord.framework.logging.FrameWorkLogger;

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
