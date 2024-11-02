package dev.jsinco.discord.framework.commands;

import dev.jsinco.discord.framework.logging.FrameWorkLogger;
import dev.jsinco.discord.framework.FrameWork;
import dev.jsinco.discord.framework.settings.Settings;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction;
import org.jetbrains.annotations.Nullable;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

/**
 * Manages all registered commands.
 * @since 1.0
 * @author Jonah
 * @see CommandModule
 * @see DiscordCommand
 */
public class CommandManager extends TimerTask {

    private static final Map<String, CommandModule> COMMAND_MODULE_MAP = new HashMap<>();
    private final Settings settings = FrameWork.getFileManager().getSettings();


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

    private static void upsertCommand(String name, String desc, List<OptionData> options, @Nullable Guild guild) {
        JDA discordApp = FrameWork.getDiscordApp();
        CommandCreateAction action = guild == null ? discordApp.upsertCommand(name, desc) : guild.upsertCommand(name, desc);
        action.addOptions(options);
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
            FrameWorkLogger.error("An exception occurred while executing command: " + event.getName());
            if (event.isAcknowledged()) {
                return;
            }

            if (settings.isSendErrors()) {
                event.replyEmbeds(new EmbedBuilder()
                        .setDescription(getMdFormattedStackTrace(throwable))
                        .addField("Cause", throwable.getCause().getMessage(), true)
                        .addField("Message", throwable.getMessage(), true)
                        .setTitle("**An exception occurred while executing this command**")
                        .setColor(Color.PINK).build()).addActionRow(
                        Button.of(ButtonStyle.PRIMARY, "commandmanager-show-errors", "Show Exceptions?", Emoji.fromUnicode("U+1F6A6"))
                ).queue();
            } else {
                event.replyEmbeds(new EmbedBuilder()
                        .setTitle("An exception occurred while executing this command")
                        .setColor(Color.PINK).build())
                        .addActionRow(
                                Button.of(ButtonStyle.PRIMARY, "commandmanager-show-errors", "Show Exceptions?", Emoji.fromUnicode("U+1F6A6"))
                        ).queue();
            }

        }
    }

    @SubscribeEvent
    public void onButtonClick(ButtonInteractionEvent event) {
        if (!"commandmanager-show-errors".equals(event.getComponentId()))  {
            return;
        }


        settings.setSendErrors(!settings.isSendErrors());
        settings.save();
        event.reply("Exceptions will now be **" + (settings.isSendErrors() ? "shown" : "hidden") + "**.").setEphemeral(true).queue();
    }

    @Override
    public void run() {
        // re-register commands in case of expiration
        COMMAND_MODULE_MAP.values().forEach(CommandManager::registerCommand);
    }

    private String getMdFormattedStackTrace(Throwable throwable) {
        StringBuilder builder = new StringBuilder();
        for (StackTraceElement element : throwable.getStackTrace()) {
            if (element.getClass().getPackage() == FrameWork.getCaller().getPackage()) {
                builder.append(element).append("\n");
            } else {
                builder.append('(').append(element).append(')').append('[').append(getGithubFileUrlFromStackElement(element)).append(']').append("\n");
            }
        }
        return builder.toString();
    }

    public String getGithubFileURLFromClass(Class<?> clazz) {
        return settings.getRepository() + "blob/" + settings.getBranch() + "/" + clazz.getName().replace(".", "/") + ".java";
    }

    public String getGithubFileUrlFromStackElement(StackTraceElement element) {
        return settings.getRepository() + "blob/" + settings.getBranch() + "/" + element.getClassName().replace(".", "/") + ".java#L" + element.getLineNumber();
    }
}
