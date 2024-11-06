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
import dev.jsinco.discord.framework.settings.Settings;
import dev.jsinco.discord.framework.shutdown.ShutdownManager;
import dev.jsinco.discord.framework.shutdown.ShutdownSavable;
import dev.jsinco.discord.framework.util.AbstainRegistration;
import dev.jsinco.discord.framework.util.AutoInstantiated;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Timer;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * The main class for the framework. Creates our JDA instance and registers all commands and events.
 * @since 1.0
 * @author Jonah
 */
public final class FrameWork {

    @Getter private static JDA jda;
    @Getter private static Timer timer;
    @Getter private static Class<?> caller;
    @Getter private static Path dataFolderPath;

    private static final int MINIMUM_BOT_TOKEN_LENGTH = 50; // According to google it's 59 characters long. But I'll just use 50.

    public static void start(Class<?> caller, Path dataFolderPath) {
        FrameWork.caller = caller;
        FrameWork.dataFolderPath = dataFolderPath;
        System.out.println("Starting " + caller.getCanonicalName());

        String botToken = System.getProperty("botToken");
        if (botToken == null) {
            botToken = System.getenv("botToken");
        }

        FrameWorkLogger.configureLogging();

        if (botToken == null || botToken.length() < MINIMUM_BOT_TOKEN_LENGTH) {
            FrameWorkLogger.error("You must provide a Discord Bot token to run this application!");
            FrameWorkLogger.error("Use JVM argument: -DbotToken=\"YOUR_BOT_TOKEN\" or provide a bot token as environment variable: 'botToken'");
            System.exit(0);
        }

        botToken = botToken.replace(" ", "").trim();
        timer = new Timer(caller.getSimpleName().toLowerCase() + "-scheduler");

        Settings settings = Settings.getInstance();
        jda = JDABuilder.createDefault(botToken)
                .enableIntents(
                        // Enable all intents for modules.
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.MESSAGE_CONTENT,
                        GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.GUILD_WEBHOOKS,
                        GatewayIntent.GUILD_MESSAGE_TYPING,
                        GatewayIntent.DIRECT_MESSAGE_TYPING
                )
                .setChunkingFilter(ChunkingFilter.ALL)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setAutoReconnect(true)
                .setStatus(settings.getDefaultStatus())
                .setActivity(settings.getDefaultActivityType() == null ? null : Activity.of(settings.getDefaultActivityType(), settings.getDefaultActivity()))
                .build();

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
        ShutdownManager.registerShutdownHook();
    }

    public static void reflectivelyRegisterClasses() {
        Set<Class<?>> classes = ReflectionUtil.getAllClassesFor(ListenerModule.class, CommandModule.class, Tickable.class, ShutdownSavable.class, AutoInstantiated.class);

        for (Class<?> aClass : classes) {
            if (aClass.isInterface()) {
                continue;
            }

            try {
                Object instance = aClass.getDeclaredConstructor().newInstance();
                if (ListenerModule.class.isAssignableFrom(aClass)) {
                    ListenerModule listenerModule = (ListenerModule) instance;
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

                if (CommandModule.class.isAssignableFrom(aClass)) {
                    CommandModule commandModule = (CommandModule) instance;
                    FrameWorkLogger.info("Registering command module (" + commandModule.getClass().getSimpleName() + ")");
                    commandModule.register();
                }
                if (Tickable.class.isAssignableFrom(aClass)) {
                    Tickable timerTickable = (Tickable) instance;
                    FrameWorkLogger.info("Registering timer tickable (" + timerTickable.getClass().getSimpleName() + ")");
                    timer.schedule(timerTickable, timerTickable.getDelay(), timerTickable.getPeriod());
                }
                if (ShutdownSavable.class.isAssignableFrom(aClass)) {
                    ShutdownSavable shutdownSavable = (ShutdownSavable) instance;
                    FrameWorkLogger.info("Registering shutdown savable (" + shutdownSavable.getClass().getSimpleName() + ")");
                    ShutdownManager.registerSavable(shutdownSavable);
                }
                if (AutoInstantiated.class.isAssignableFrom(aClass)) {
                    ((AutoInstantiated) instance).onInstantiation();
                    FrameWorkLogger.info("Auto-instantiated class: " + aClass.getCanonicalName());
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

    public static <T> T registerTickable(T tickable) {
        if (Tickable.class.isAssignableFrom(tickable.getClass())) {
            Tickable tickable1 = (Tickable) tickable;
            timer.schedule(tickable1, tickable1.getDelay(), tickable1.getPeriod());
        }
        return tickable;
    }

}
