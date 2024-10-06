package dev.jsinco.discord.console.commands;

import dev.jsinco.discord.FrameWork;
import dev.jsinco.discord.console.ConsoleCommand;
import dev.jsinco.discord.logging.FrameWorkLogger;
import net.dv8tion.jda.api.JDA;

public class DumpJDAInfoCommand implements ConsoleCommand {
    @Override
    public String name() {
        return "dumpjdainfo";
    }

    @Override
    public void execute(String[] args) {
        JDA jda = FrameWork.getDiscordApp();

        FrameWorkLogger.info("JDA Info:");
        FrameWorkLogger.info("  - Status: " + jda.getStatus());
        FrameWorkLogger.info("  - Gateway Ping: " + jda.getGatewayPing());
        FrameWorkLogger.info("  - Guilds: " + jda.getGuilds().size());
        FrameWorkLogger.info("  - Users: " + jda.getUsers().size());
        FrameWorkLogger.info("  - Categories: " + jda.getCategories().size());
        FrameWorkLogger.info("  - Roles: " + jda.getRoles().size());
        FrameWorkLogger.info("  - Voice Channels: " + jda.getVoiceChannels().size());
        FrameWorkLogger.info("  - Text Channels: " + jda.getTextChannels().size());
    }
}
