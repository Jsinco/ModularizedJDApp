package dev.jsinco.discord.framework.commands;

import dev.jsinco.discord.framework.reflect.ReflectionUtil;
import dev.jsinco.discord.framework.logging.FrameWorkLogger;
import dev.jsinco.discord.framework.FrameWork;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

/**
 * Manages all registered commands.
 * @since 1.0
 * @author Jonah
 * @see CommandModule
 * @see CommandOption
 * @see DiscordCommand
 */
public class CommandManager extends TimerTask {

    private static final Map<String, CommandModule> COMMAND_MODULE_MAP = new HashMap<>();


    public static CommandModule getCommand(String name) {
        return COMMAND_MODULE_MAP.get(name);
    }


    public static void registerCommand(CommandModule commandModule) {
        JDA discordApp = FrameWork.getDiscordApp();

        if (commandModule.getCommandInfo() == null) {
            throw new IllegalArgumentException("CommandModule must have a DiscordCommand annotation!");
        }

        if (commandModule.getCommandInfo().guildOnly()) {
            for (Guild guild : discordApp.getGuilds()) {
                upsertCommand(commandModule.getCommandInfo().name(), commandModule.getCommandInfo().description(), commandModule.getOptions(), guild);
            }
        } else {
            upsertCommand(commandModule.getCommandInfo().name(), commandModule.getCommandInfo().description(), commandModule.getOptions(), null);
        }

        COMMAND_MODULE_MAP.put(commandModule.getCommandInfo().name(), commandModule);
    }

    private static void upsertCommand(String name, String desc, List<CommandOption> options, @Nullable Guild guild) {
        JDA discordApp = FrameWork.getDiscordApp();
        CommandCreateAction action = guild == null ? discordApp.upsertCommand(name, desc) : guild.upsertCommand(name, desc);
        for (CommandOption option : options) {
            action.addOption(option.getOptionType(), option.getName(), option.getDescription(), option.isRequired());
        }
        action.queue();
    }

    @SubscribeEvent
    public void onSlashCommandEvent(SlashCommandInteractionEvent event) {
        CommandModule command = COMMAND_MODULE_MAP.get(event.getName());
        if (command == null) {
            return;
        }
        if (!event.getMember().hasPermission(command.getCommandInfo().permission())) {
            event.reply("You do not have permission to use this command!\n-# Permission Node: " + command.getCommandInfo().permission().getName()).queue();
            return;
        }
        try {
            command.execute(event);
        } catch (Throwable throwable) {
            FrameWorkLogger.error("An error occurred while executing command: " + event.getName(), throwable);
        }
    }

    @Override
    public void run() {
        // re-register commands in case of expiration
        COMMAND_MODULE_MAP.values().forEach(CommandManager::registerCommand);
    }
}
