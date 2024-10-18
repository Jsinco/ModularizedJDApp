package club.coding.discord.framework;

import club.coding.discord.framework.commands.CommandManager;
import club.coding.discord.framework.console.ConsoleCommandManager;
import club.coding.discord.framework.console.commands.DumpJDAInfoCommand;
import club.coding.discord.framework.console.commands.HelpCommand;
import club.coding.discord.framework.console.commands.StopCommand;
import club.coding.discord.framework.events.EventManager;
import club.coding.discord.framework.logging.FrameWorkLogger;
import club.coding.discord.framework.reflect.InjectStatic;
import club.coding.discord.framework.reflect.ReflectionUtil;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The main class for the framework. Creates our JDA instance and registers all commands and events.
 * @since 1.0
 * @author Jonah
 */
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
            System.exit(0);
        }

        botToken = botToken.replace(" ", "").trim();

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
        discordApp.addEventListener(commandManager); // Manually add this event listener

        // Console commands
        ConsoleCommandManager.getInstance()
                .registerCommand(new StopCommand())
                .registerCommand(new HelpCommand())
                .registerCommand(new DumpJDAInfoCommand());

        // I think this is the best point to inject static fields
        injectStaticFields();
    }

    public static void injectStaticFields() {
        List<Class<?>> classes = ReflectionUtil.getAllClassesFor(null);
        for (Class<?> caller : classes) {
            for (Field callerField : caller.getDeclaredFields()) {
                InjectStatic annotation = callerField.getAnnotation(InjectStatic.class);

                if (annotation == null || !Modifier.isStatic(callerField.getModifiers())) {
                    continue;
                }
                Class<?> from = annotation.from();
                if (from == null) {
                    continue;
                }


                AtomicReference<String> name = new AtomicReference<>();
                if (Arrays.stream(from.getDeclaredFields()).anyMatch(f -> {
                    name.set(f.getName());
                    return f.getType().equals(callerField.getType());
                })) {
                    try {
                        Field fromField = annotation.specificField().isEmpty() ? from.getDeclaredField(name.get()) : from.getDeclaredField(annotation.specificField());
                        fromField.setAccessible(true);
                        callerField.setAccessible(true);
                        callerField.set(caller, fromField.get(from));
                    } catch (Exception e) {
                        FrameWorkLogger.error("An error occurred while injecting field " + callerField.getName(), e);
                    }
                }

            }
        }
    }


}
