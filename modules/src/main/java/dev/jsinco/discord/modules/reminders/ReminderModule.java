package dev.jsinco.discord.modules.reminders;

import dev.jsinco.abstractjavafilelib.schemas.SnakeYamlConfig;
import dev.jsinco.discord.framework.commands.CommandModule;
import dev.jsinco.discord.framework.logging.FrameWorkLogger;
import dev.jsinco.discord.framework.scheduling.TimeUnit;
import dev.jsinco.discord.framework.scheduling.Tick;
import dev.jsinco.discord.framework.scheduling.Tickable;
import dev.jsinco.discord.framework.commands.CommandOption;
import dev.jsinco.discord.framework.commands.DiscordCommand;
import dev.jsinco.discord.framework.reflect.InjectStatic;
import dev.jsinco.discord.modules.Main;
import lombok.Getter;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Tick(unit = TimeUnit.SECONDS, period = 10)
@DiscordCommand(name = "reminder", permission = Permission.MANAGE_CHANNEL,
        description = "Schedule a message to be sent at a later time, or to be repeated at certain times.")
public class ReminderModule extends Tickable implements CommandModule {

    @Getter
    private static final List<WrappedReminder> WRAPPED_REMINDERS = new ArrayList<>();
    private static final String SAVE_REGION = "scheduledMessages";
    @InjectStatic(from = Main.class)
    private static SnakeYamlConfig savesFile;

    public ReminderModule() {
        if (savesFile.contains(SAVE_REGION)) {
            WRAPPED_REMINDERS.addAll(savesFile.getStringList(SAVE_REGION).stream().map(WrappedReminder::deserialize).toList());
        }
    }


    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String identifier = null;
        if (event.getOption("id") != null) {
            identifier = event.getOption("id").getAsString();
        }
        Channel channel = event.getOption("channel").getAsChannel();
        String message = event.getOption("message").getAsString();
        MessageFrequency frequency = new MessageFrequency(event.getOption("frequency").getAsString().toUpperCase());
        String when = event.getOption("when").getAsString();

        WrappedReminder wrappedReminder = identifier == null ? new WrappedReminder(channel, message, frequency, when) : new WrappedReminder(identifier, channel, message, frequency, when);
        WRAPPED_REMINDERS.add(wrappedReminder);
        event.reply("Scheduled message for " + when + " in " + channel.getAsMention() + " **Frequency " + frequency + "**").queue();
    }


    @Override
    public List<CommandOption> getOptions() {
        return List.of(
                CommandOption.builder().optionType(OptionType.CHANNEL).name("channel").required(true).description("The channel to send the message in.").build(),
                CommandOption.builder().optionType(OptionType.STRING).name("message").required(true).description("The message to send.").build(),
                CommandOption.builder().optionType(OptionType.STRING).name("when").required(true).description("Set when this message should be sent: MM-DD-YYYY|HH:MM").build(),
                CommandOption.builder().optionType(OptionType.STRING).name("frequency").required(true)
                        .description("Set the frequency for this message to be repeated: NEVER, 10SEC, 1MIN, 1HR, 3DAY, 1WEEK, 2MONTH").build(),
                CommandOption.builder().optionType(OptionType.STRING).name("id").required(false).description("The identifier of this scheduled message.").build()
        );
    }

    
    @Override
    public void onTick() {
        if (WRAPPED_REMINDERS.isEmpty()) return;

        for (WrappedReminder message : WRAPPED_REMINDERS) {
            if (!message.shouldSendNow()) {
                continue;
            }

            TextChannel channel = (TextChannel) message.getChannel();
            channel.sendMessage(message.getMessage() + "\n-# (reminder id: " + message.getIdentifier() + ")").queue();
            message.setLastSent(LocalDateTime.now());
            FrameWorkLogger.info("Sent scheduled message in " + channel.getName() + " at " + LocalDateTime.now() + " with frequency " + message.getFrequency());
        }

        if (savesFile.getStringList(SAVE_REGION).size() != WRAPPED_REMINDERS.size()) {
            savesFile.set(SAVE_REGION, WRAPPED_REMINDERS.stream().map(WrappedReminder::serialize).toList());
            savesFile.save();
        }
    }
}

