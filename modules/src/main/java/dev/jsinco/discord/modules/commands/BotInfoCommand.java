package dev.jsinco.discord.modules.commands;

import dev.jsinco.discord.framework.commands.CommandModule;
import dev.jsinco.discord.framework.commands.DiscordCommand;
import dev.jsinco.abstractjavafilelib.schemas.SnakeYamlConfig;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.ArrayList;
import java.util.List;

public class BotInfoCommand implements CommandModule {

    private final SnakeYamlConfig file;

    public BotInfoCommand() {
        file = new SnakeYamlConfig("project.yml", true);
    }

    public String getVersion() {
        String s = file.getString("version");
        return s == null ? "${version}" : s;
    }
    public List<String> getAuthors() {
        List<String> authors = new ArrayList<>();
        List<String> keys = file.getConfigurationSection("authors").getKeys();
        for (String key : keys) {
            authors.add("[" + key + "](" + file.getString("authors." + key) + ")");
        }
        return authors;
    }

    @DiscordCommand(name = "info", description = "Displays bot information", guildOnly = false)
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.reply("Bot Info: \n" +
                "  * Version: " + getVersion() + "\n" +
                "  * Authors: " + getAuthors()).queue();
    }
}
