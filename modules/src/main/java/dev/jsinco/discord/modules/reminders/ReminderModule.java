package dev.jsinco.discord.modules.reminders;

import dev.jsinco.abstractjavafilelib.schemas.SnakeYamlConfig;
import dev.jsinco.discord.framework.logging.FrameWorkLogger;
import dev.jsinco.discord.framework.scheduling.TimeUnit;
import dev.jsinco.discord.framework.scheduling.Tick;
import dev.jsinco.discord.framework.scheduling.Tickable;
import dev.jsinco.discord.framework.commands.DiscordCommand;
import dev.jsinco.discord.framework.reflect.InjectStatic;
import dev.jsinco.discord.framework.serdes.Serdes;
import dev.jsinco.discord.framework.util.Module;
import dev.jsinco.discord.modules.Main;
import dev.jsinco.discord.modules.util.Util;
import lombok.Getter;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

@Tick(unit = TimeUnit.SECONDS, period = 30)
@DiscordCommand(name = "reminder", permission = Permission.MANAGE_CHANNEL,
        description = "Schedule a message to be sent at a later time, or to be repeated at certain times.")
public class ReminderModule extends Tickable implements Module {

    @Getter
    private static final ConcurrentLinkedQueue<WrappedReminder> WRAPPED_REMINDERS = new ConcurrentLinkedQueue<>();
    private static final String SAVE_REGION = "scheduledMessages";
    @InjectStatic(value = Main.class)
    private static SnakeYamlConfig savesFile;

    private final Serdes serdes = Serdes.getSingleton();

    public ReminderModule() {
        if (savesFile.contains(SAVE_REGION)) {
            WRAPPED_REMINDERS.addAll(savesFile.getStringList(SAVE_REGION).stream().map(it -> serdes.deserialize(it, WrappedReminder.class)).toList());
        }
    }


    @Override
    public void execute(SlashCommandInteractionEvent event) {

        String message = Util.getOptionOrNull(event.getOption("message"), OptionType.STRING);
        String time = Util.getOptionOrNull(event.getOption("time"), OptionType.STRING);
        Channel channel = Util.getOptionOrNull(event.getOption("channel"), OptionType.CHANNEL, event.getChannel());
        String date = Util.getOptionOrNull(event.getOption("date"), OptionType.STRING);
        MessageFrequency.MessageFrequencyUnit repeat = Util.getEnumByName(MessageFrequency.MessageFrequencyUnit.class, Util.getOptionOrNull(event.getOption("repeat"), OptionType.STRING, "NEVER"));
        int interval = Util.getOptionOrNull(event.getOption("interval"), OptionType.INTEGER, 1);
        String identifier = Util.getOptionOrNull(event.getOption("id"), OptionType.STRING, "reminder #" + WRAPPED_REMINDERS.size());

        WrappedReminder wrappedReminder = new WrappedReminder.WrappedReminderBuilder()
                .identifier(identifier)
                .channel((TextChannel) channel)
                .message(message)
                .frequency(new MessageFrequency(interval, repeat))
                .when(Util.parseDateTime(date, time))
                .build();
        WRAPPED_REMINDERS.add(wrappedReminder);

        StringBuilder sb = new StringBuilder("Reminder Scheduled\n");
        sb.append("- Date: **" + wrappedReminder.getWhen().toLocalDate() + "**\n");
        sb.append("- Time: **" + wrappedReminder.getWhen().toLocalTime() + "**\n");
        sb.append("- Channel: <#" + channel.getId() + ">\n");
        sb.append("- Frequency: **" + interval + ";" + repeat + "**\n");
        sb.append("- Message: `" + message + "`\n");

        event.reply(sb.toString())
                .addActionRow(
                        Button.of(ButtonStyle.DANGER, "remindermodule-remove;" + identifier, "Delete Reminder", Emoji.fromUnicode("U+1F5D1"))
                ).queue();
    }

    @SubscribeEvent
    public void onButtonClick(ButtonInteractionEvent event) {
        if (!event.getComponentId().startsWith("remindermodule-remove;")) return;

        String identifier = event.getComponentId().split(";")[1];
        WRAPPED_REMINDERS.removeIf(it -> it.getIdentifier().equals(identifier));
        event.reply("Reminder removed. \n-# Id: " + identifier).queue();
    }


    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.STRING, "message", "The message to send.").setRequired(true),
                new OptionData(OptionType.STRING, "time", "Set when this message should be sent: HH:MM. IN 24 HOUR TIME!!! ").setRequired(true),
                new OptionData(OptionType.CHANNEL, "channel", "The channel to send the message in.").setRequired(false),
                new OptionData(OptionType.STRING, "date", "Set when this message should be sent: MM/DD/YYYY").setMaxLength(10).setRequired(false),
                new OptionData(OptionType.STRING, "repeat", "Set the frequency for this message to be repeated").addChoices(Util.buildChoicesFromEnum(MessageFrequency.MessageFrequencyUnit.class)).setRequired(false),
                new OptionData(OptionType.INTEGER, "interval", "Set the interval for this message to be repeated").setRequired(false),
                new OptionData(OptionType.STRING, "id", "The identifier of this scheduled message.").setRequired(false)
        );
    }

    
    @Override
    public void onTick() {
        if (WRAPPED_REMINDERS.isEmpty()) return;

        for (WrappedReminder message : WRAPPED_REMINDERS) {
            if (!message.shouldSendNow()) {
                if (!message.isValid()) {
                    WRAPPED_REMINDERS.remove(message);
                }
                continue;
            }

            message.send();
            FrameWorkLogger.info("Sent scheduled message in " + message.getChannel().getName() + " at " + LocalDateTime.now() + " with frequency " + message.getFrequency());
        }

        savesFile.set(SAVE_REGION, WRAPPED_REMINDERS.stream().map(serdes::serialize).toList());
        savesFile.save();
    }
}

