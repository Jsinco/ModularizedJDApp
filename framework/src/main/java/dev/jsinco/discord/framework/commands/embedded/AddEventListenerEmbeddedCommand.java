package dev.jsinco.discord.framework.commands.embedded;

import dev.jsinco.discord.framework.commands.CommandModule;
import dev.jsinco.discord.framework.commands.DiscordCommand;
import dev.jsinco.discord.framework.events.EventManager;
import dev.jsinco.discord.framework.events.ListenerModuleState;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

@DiscordCommand(name = "addevent", description = "Add a class as an event listener.", permission = Permission.ADMINISTRATOR)
public class AddEventListenerEmbeddedCommand implements CommandModule {

    private int registrationHash = EventManager.getListeners().hashCode();

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String className = event.getOption("class").getAsString();
        for (var classModule : EventManager.getListeners(ListenerModuleState.INACTIVE)) {
            if (classModule.getClass().getName().equals(className)) {
                classModule.register();
                event.reply("Successfully registered **" + className + "** as an event listener.").queue();
                return;
            }
        }
        event.reply("Could not find class **" + className + "** in standby modules.").queue();
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.STRING, "class", "The class to add as an event listener.", true).addChoices(this.getStandByClasses())
        );
    }

    private List<Command.Choice> getStandByClasses() {
        return EventManager.getListeners(ListenerModuleState.INACTIVE).stream()
                .map(it -> new Command.Choice(it.getClass().getName(), it.getClass().getName()))
                .toList();
    }

    @Override
    public boolean persistRegistration() {
        if (EventManager.getListeners().hashCode() == registrationHash) {
            registrationHash = EventManager.getListeners().hashCode();
            return false;
        }
        return true;
    }
}
