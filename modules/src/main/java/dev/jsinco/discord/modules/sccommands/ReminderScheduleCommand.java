package dev.jsinco.discord.modules.sccommands;

import dev.jsinco.abstractjavafilelib.schemas.SnakeYamlConfig;
import dev.jsinco.discord.framework.commands.CommandModule;
import dev.jsinco.discord.framework.commands.CommandOption;
import dev.jsinco.discord.framework.reflect.InjectStatic;
import dev.jsinco.discord.modules.Main;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.util.List;

public class ReminderScheduleCommand implements CommandModule {

    @InjectStatic(from = Main.class)
    private static SnakeYamlConfig savedReminderDates;

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        
    }

    @Override
    public List<CommandOption> getOptions() {
        return List.of(new CommandOption("reminder", OptionType.STRING, "date time format", true));
    }
}
