package dev.jsinco.discord.modules.moduleimpl.webhooks;

import dev.jsinco.discord.framework.commands.CommandModule;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

public class WebhookMessageCommand implements CommandModule {
    @Override
    public void execute(SlashCommandInteractionEvent event) {

    }

    @Override
    public List<OptionData> getOptions() {
        return CommandModule.super.getOptions();
    }
}
