package dev.jsinco.discord.modules.moduleimpl.canvas.commands;

import dev.jsinco.discord.framework.commands.DiscordCommand;
import dev.jsinco.discord.modules.moduleimpl.canvas.CanvasFactoryManager;
import dev.jsinco.discord.modules.moduleimpl.canvas.DiscordCanvasUser;
import dev.jsinco.discord.modules.moduleimpl.canvas.Institution;
import dev.jsinco.discord.modules.moduleimpl.canvas.moduleabstract.interfaces.CanvasCommandModule;
import edu.ksu.canvas.CanvasApiFactory;
import edu.ksu.canvas.interfaces.DiscussionTopicReader;
import edu.ksu.canvas.model.DiscussionTopic;
import edu.ksu.canvas.requestOptions.ListDiscussionTopicsOptions;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.io.IOException;
import java.util.List;

@DiscordCommand(name = "canvas-announcements", description = "Show the announcements for a course on Canvas")
public class CanvasAnnouncementsCommand implements CanvasCommandModule {
    @Override
    public void canvasCommand(SlashCommandInteractionEvent event, DiscordCanvasUser user, boolean ephemeral) throws IOException {
        event.deferReply(ephemeral).queue();

        Institution institution = user.getInstitution();
        CanvasApiFactory factory = CanvasFactoryManager.getFactory(institution);

        DiscussionTopicReader reader = factory.getReader(DiscussionTopicReader.class, user.getOauth());
        List<DiscussionTopic> topics = reader.listDiscussionTopics(
                new ListDiscussionTopicsOptions("141571", ListDiscussionTopicsOptions.IdType.COURSES)
                        .onlyAnnouncements()
        );

        StringBuilder response = new StringBuilder("**Announcements:**\n");
        for (DiscussionTopic topic : topics) {
            response.append("  * ").append(topic.getTitle()).append("\n");
        }

        EmbedBuilder embedBuilder = institution.getEmbed();
        embedBuilder.setTitle("Canvas Announcements");
        embedBuilder.setDescription(response.toString());
        event.getHook().sendMessageEmbeds(embedBuilder.build())
                .addFiles(institution.getCanvasLogoFileUpload())
                .setEphemeral(ephemeral).queue();
    }
}
