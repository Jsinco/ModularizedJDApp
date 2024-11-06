package dev.jsinco.discord.modules.moduleimpl.embeds;

import dev.jsinco.discord.framework.commands.CommandModule;
import dev.jsinco.discord.framework.commands.DiscordCommand;
import dev.jsinco.discord.modules.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.utils.data.DataObject;

import java.util.List;

@DiscordCommand(name = "embedjson", description = "Send an embed from a JSON string", permission = Permission.MANAGE_CHANNEL)
public class EmbedJsonCommand implements CommandModule {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String jsonString = Util.getOption(event.getOption("json"), OptionType.STRING, "Should not be null");
        TextChannel channel = Util.getOption(event.getOption("channel"), OptionType.CHANNEL, event.getChannel()).asTextChannel();

        DataObject json = DataObject.fromJson(jsonString); // load the json data
        channel.sendMessageEmbeds(EmbedBuilder.fromData(json).setAuthor(event.getUser().getName(), null, event.getUser().getAvatarUrl()).build()).queue();
        event.reply("Embed sent!").setEphemeral(true).queue();
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.STRING, "json", "The raw JSON of the embed. See an embed builder website: https://embed.dan.onl", true),
                new OptionData(OptionType.CHANNEL, "channel", "The channel to send the embed to", false)
        );
    }
}
