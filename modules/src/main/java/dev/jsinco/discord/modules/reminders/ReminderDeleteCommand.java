package dev.jsinco.discord.modules.reminders;

import dev.jsinco.discord.framework.commands.CommandModule;
import dev.jsinco.discord.framework.commands.DiscordCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.util.List;

public class ReminderDeleteCommand implements CommandModule {

    @DiscordCommand(name = "reminder-delete", description = "Delete a scheduled reminder message.", permission = Permission.MANAGE_CHANNEL)
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        OptionMapping optionMapping = event.getOption("id");
        if (optionMapping == null) {
            event.reply("All scheduled messages:" + ReminderModule.getWRAPPED_REMINDERS().stream().map(it -> "\n" + it.getIdentifier() + " **-->** " + it.getMessage()).toList()).queue();
        } else {
            for (var scheduledMessage : List.copyOf(ReminderModule.getWRAPPED_REMINDERS())) {
                if (scheduledMessage.getIdentifier().equalsIgnoreCase(optionMapping.getAsString())) {
                    ReminderModule.getWRAPPED_REMINDERS().remove(scheduledMessage);
                    event.reply("Scheduled message with id " + scheduledMessage.getIdentifier() + " has been removed.").queue();
                }
            }
        }
    }

//    @Override
//    public List<OptionBuilder> getOptions() {
//        return List.of(OptionBuilder.builder().name("id").optionType(OptionType.STRING).description("The identifier of the scheduled message to delete.").required(false).build());
//    }
}
