package dev.jsinco.discord;

import dev.jsinco.discord.commands.CommandManager;
import dev.jsinco.discord.console.ConsoleCommandManager;
import dev.jsinco.discord.console.commands.DumpJDAInfoCommand;
import dev.jsinco.discord.console.commands.HelpCommand;
import dev.jsinco.discord.console.commands.StopCommand;
import dev.jsinco.discord.events.EventManager;
import dev.jsinco.discord.logging.FrameWorkLogger;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import java.util.Scanner;
import java.util.Timer;

public final class FrameWork {

    private static JDA discordApp;

    public static JDA getDiscordApp() {
        return discordApp;
    }

    public static void start(Class<?> caller) {
        System.out.println("Starting " + caller.getCanonicalName());

        String botToken = System.getenv("botToken");
        FrameWorkLogger.configureLogging();

        if (botToken == null) {
            FrameWorkLogger.error("You must provide a Discord Bot token to run this application!");
            FrameWorkLogger.error("Use JVM argument: -DbotToken=\"YOUR_BOT_TOKEN\"");
            System.exit(1);
        }

        discordApp = JDABuilder.createDefault(botToken)
                .enableIntents(GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.MESSAGE_CONTENT,
                        GatewayIntent.GUILD_MEMBERS)
                .setChunkingFilter(ChunkingFilter.ALL)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setAutoReconnect(true).build();

        try {
            discordApp.awaitReady();
            FrameWorkLogger.info("JDA ready!");
        } catch (InterruptedException e) {
            FrameWorkLogger.error("An error occurred while waiting for the JDA to be ready!", e);
        }
        
        // Using annotations is so much better
        discordApp.setEventManager(new AnnotatedEventManager());
        EventManager.reflectivelyRegisterEvents();
        CommandManager.reflectivelyRegisterCommands();

        // Re-register expired commands
        Timer timer = new Timer();
        CommandManager commandManager = new CommandManager();
        timer.schedule(commandManager, 0L, 300000L);
        discordApp.addEventListener(commandManager);

        // Console commands
        ConsoleCommandManager.getInstance()
                .registerCommand(new StopCommand())
                .registerCommand(new HelpCommand())
                .registerCommand(new DumpJDAInfoCommand());
    }


}
