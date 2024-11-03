package dev.jsinco.discord.modules.reminders;

import dev.jsinco.abstractjavafilelib.schemas.SnakeYamlConfig;
import dev.jsinco.discord.framework.commands.CommandModule;
import dev.jsinco.discord.framework.commands.DiscordCommand;
import dev.jsinco.discord.framework.reflect.InjectStatic;
import dev.jsinco.discord.modules.Main;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Command module class for deleting reminders.
 * @see MessageFrequency
 * @see ReminderModule
 * @see WrappedReminder
 * @see WrappedReminderTypeAdapter
 * @author Jonah
 */
public class ReminderDeleteCommand implements CommandModule {

    @InjectStatic(ReminderModule.class)
    private static ConcurrentLinkedQueue<WrappedReminder> wrappedReminders;
    @InjectStatic(Main.class)
    private static SnakeYamlConfig saves;
    @InjectStatic(value = ReminderModule.class, specificField = "SAVE_REGION")
    private static String SAVE_REGION;

    private int activeReminders;

    @DiscordCommand(name = "reminder-delete", description = "Delete a scheduled reminder message.", permission = Permission.MANAGE_CHANNEL)
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String id = event.getOption("id").getAsString();
        Guild guild = event.getGuild();

        if (guild == null) {
            event.reply("This command can only be used in a guild.").setEphemeral(true).queue();
            return;
        }

        for (var scheduledMessage : wrappedReminders) {
            if (scheduledMessage.getIdentifier().equalsIgnoreCase(id) && guild.getChannels().contains(scheduledMessage.getChannel())) {
                wrappedReminders.remove(scheduledMessage);
                event.reply("Scheduled message with id **" + scheduledMessage.getIdentifier() + "** has been removed.").queue();
                return;
            }
        }
        event.reply("No reminder with id **" + id + "** was found.").queue();
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.STRING, "id", "The identifier of the reminder to remove.").addChoices(getReminderChoices()).setRequired(true)
        );
    }

    private List<Command.Choice> getReminderChoices() {
//        if (wrappedReminders.isEmpty()) {
//            return List.of(new Command.Choice("no reminders available to delete", "none"));
//        }


        return wrappedReminders.stream().map(it -> {
            String s = it.getIdentifier().toLowerCase();
            return new Command.Choice(s, s);
        }).toList();
    }

    @Override
    public boolean persistRegistration() {
        if (wrappedReminders.size() != activeReminders) {
            activeReminders = wrappedReminders.size();
            return true;
        }
        return false;
    }
}
