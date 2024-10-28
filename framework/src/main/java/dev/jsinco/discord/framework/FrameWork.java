package dev.jsinco.discord.framework;

import dev.jsinco.discord.framework.commands.CommandManager;
import dev.jsinco.discord.framework.commands.CommandModule;
import dev.jsinco.discord.framework.console.ConsoleCommandManager;
import dev.jsinco.discord.framework.console.commands.DumpJDAInfoCommand;
import dev.jsinco.discord.framework.console.commands.HelpCommand;
import dev.jsinco.discord.framework.console.commands.RestartCommand;
import dev.jsinco.discord.framework.console.commands.StopCommand;
import dev.jsinco.discord.framework.events.ListenerModule;
import dev.jsinco.discord.framework.logging.FrameWorkLogger;
import dev.jsinco.discord.framework.reflect.InjectStatic;
import dev.jsinco.discord.framework.reflect.ReflectionUtil;
import dev.jsinco.discord.framework.scheduling.Tickable;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Set;
import java.util.Timer;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The main class for the framework. Creates our JDA instance and registers all commands and events.
 * @since 1.0
 * @author Jonah
 */
public final class FrameWork {

    @Getter private static JDA discordApp;
    @Getter private static Timer timer;
    @Getter private static Class<?> caller;
    private static final int MINIMUM_BOT_TOKEN_LENGTH = 50; // According to google it's 59 characters long. But I'll just use 50.

    public static void start(Class<?> caller) {
        FrameWork.caller = caller;
        System.out.println("Starting " + caller.getCanonicalName());
        timer = new Timer(caller.getSimpleName().toLowerCase() + "-scheduler");

        String botToken = System.getProperty("botToken");
        if (botToken == null) {
            botToken = System.getenv("botToken");
        }

        FrameWorkLogger.configureLogging();

        if (botToken == null || botToken.length() < MINIMUM_BOT_TOKEN_LENGTH) {
            FrameWorkLogger.error("You must provide a Discord Bot token to run this application!");
            FrameWorkLogger.error("Use JVM argument: '-DbotToken=YOUR_BOT_TOKEN' or provide a bot token as environment variable: 'botToken'");
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


        // I think this is the best point to inject static fields
        injectStaticFields();

        // Using annotations is so much better
        discordApp.setEventManager(new AnnotatedEventManager());
        reflectivelyRegisterClasses();

        // Re-register expired commands
        CommandManager commandManager = new CommandManager();
        timer.schedule(commandManager, 0L, 300000L);
        discordApp.addEventListener(commandManager); // Manually add this event listener

        // Console commands
        ConsoleCommandManager.getInstance()
                .registerCommand(new StopCommand())
                .registerCommand(new HelpCommand())
                .registerCommand(new DumpJDAInfoCommand())
                .registerCommand(new RestartCommand());

    }

    public static void reflectivelyRegisterClasses() {
        Set<Class<?>> classes = ReflectionUtil.getAllClassesFor(ListenerModule.class, CommandModule.class, Tickable.class);

        int skipped = 0;
        for (Class<?> aClass : classes) {

            try {
                Object instance = aClass.getDeclaredConstructor().newInstance();
                if (instance instanceof ListenerModule listenerModule) {
                    listenerModule.register();
                    FrameWorkLogger.info("Registered listener module! (" + listenerModule.getClass().getSimpleName() + ")");
                }
                if (instance instanceof CommandModule commandModule) {
                    commandModule.register();
                    FrameWorkLogger.info("Registered command module! (" + commandModule.getClass().getSimpleName() + ")");
                }
                if (instance instanceof Tickable timerTickable) {
                    timer.schedule(timerTickable, timerTickable.getDelay(), timerTickable.getPeriod());
                    FrameWorkLogger.info("Registered timer tickable! (" + timerTickable.getClass().getSimpleName() + ")");
                }
            } catch (NoSuchMethodException ignored) {
                // If the class doesn't have a no-args constructor, the developer has to register it manually
                skipped++;
            } catch (Exception e) {
                skipped++;
                FrameWorkLogger.error("An error occurred while registering a class reflectively: " + aClass.getCanonicalName(), e);
            }
        }
        FrameWorkLogger.info("Finished registering classes/modules reflectively! Skipped " + skipped + " classes.");
    }



    public static void injectStaticFields() {
        Set<Class<?>> classes = ReflectionUtil.getAllClassesFor();
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
