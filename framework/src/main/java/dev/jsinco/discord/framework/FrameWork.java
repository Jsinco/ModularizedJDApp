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
import dev.jsinco.discord.framework.settings.FrameWorkFileManager;
import dev.jsinco.discord.framework.util.AbstainRegistration;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
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

    @Getter private static JDA jda;
    @Getter private static Timer timer;
    @Getter private static Class<?> caller;
    @Getter private static FrameWorkFileManager fileManager;

    private static final int MINIMUM_BOT_TOKEN_LENGTH = 50; // According to google it's 59 characters long. But I'll just use 50.

    public static void start(Class<?> caller, Path dataFolderPath) {
        FrameWork.caller = caller;
        System.out.println("Starting " + caller.getCanonicalName());

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
        timer = new Timer(caller.getSimpleName().toLowerCase() + "-scheduler");
        fileManager = new FrameWorkFileManager(dataFolderPath);

        jda = JDABuilder.createDefault(botToken)
                .enableIntents(GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.MESSAGE_CONTENT,
                        GatewayIntent.GUILD_MEMBERS)
                .setChunkingFilter(ChunkingFilter.ALL)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setAutoReconnect(true).build();

        try {
            jda.awaitReady();
            FrameWorkLogger.info("JDA ready!");
        } catch (InterruptedException e) {
            FrameWorkLogger.error("An error occurred while waiting for JDA to be ready!", e);
        }


        // I think this is the best point to inject static fields
        injectStaticFields();

        // Using annotations is so much better
        jda.setEventManager(new AnnotatedEventManager());
        reflectivelyRegisterClasses();

        CommandManager commandManager = new CommandManager();
        jda.addEventListener(commandManager); // Manually add this event listener
        timer.schedule(commandManager, 0L, 10000L); // 10 Sec

        // Update commands on startup
//        jda.updateCommands().queue();
//        for (Guild guild : jda.getGuilds()) {
//            guild.updateCommands().queue();
//        }

        // Console commands
        ConsoleCommandManager.getInstance()
                .registerCommand(new StopCommand())
                .registerCommand(new HelpCommand())
                .registerCommand(new DumpJDAInfoCommand())
                .registerCommand(new RestartCommand());

        // one last injection
        injectStaticFields();
    }

    public static void reflectivelyRegisterClasses() {
        Set<Class<?>> classes = ReflectionUtil.getAllClassesFor(ListenerModule.class, CommandModule.class, Tickable.class);

        for (Class<?> aClass : classes) {

            try {
                Object instance = aClass.getDeclaredConstructor().newInstance();
                if (instance instanceof ListenerModule listenerModule) {
                    if (aClass.isAnnotationPresent(AbstainRegistration.class)) {
                        listenerModule.registerInactive();
                    } else {
                        FrameWorkLogger.info("Registering listener module (" + listenerModule.getClass().getSimpleName() + ")");
                        listenerModule.register();
                    }
                }

                if (aClass.isAnnotationPresent(AbstainRegistration.class)) {
                    continue;
                }

                if (instance instanceof CommandModule commandModule) {
                    FrameWorkLogger.info("Registering command module (" + commandModule.getClass().getSimpleName() + ")");
                    commandModule.register();
                }
                if (instance instanceof Tickable timerTickable) {
                    FrameWorkLogger.info("Registering timer tickable (" + timerTickable.getClass().getSimpleName() + ")");
                    timer.schedule(timerTickable, timerTickable.getDelay(), timerTickable.getPeriod());
                }
            } catch (NoSuchMethodException ignored) {
                // If the class doesn't have a no-args constructor, the developer has to register it manually
            } catch (Exception e) {
                FrameWorkLogger.error("An error occurred while registering a class reflectively: " + aClass.getCanonicalName(), e);
            }
        }
        FrameWorkLogger.info("Finished registering classes/modules reflectively!");
    }



    public static void injectStaticFields() {
        Set<Class<?>> classes = ReflectionUtil.getAllClassesFor();
        for (Class<?> caller : classes) {
            for (Field callerField : caller.getDeclaredFields()) {
                InjectStatic annotation = callerField.getAnnotation(InjectStatic.class);

                if (annotation == null || !Modifier.isStatic(callerField.getModifiers())) {
                    continue;
                }
                Class<?> from = annotation.value();
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
