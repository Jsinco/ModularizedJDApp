package dev.jsinco.discord.modules.canvas.commands;

import dev.jsinco.discord.framework.commands.DiscordCommand;
import dev.jsinco.discord.modules.canvas.CanvasFactoryManager;
import dev.jsinco.discord.modules.canvas.encapsulation.DiscordCanvasUser;
import dev.jsinco.discord.modules.canvas.encapsulation.institute.Institution;
import dev.jsinco.discord.modules.canvas.moduleabstract.interfaces.CanvasCommand;
import dev.jsinco.discord.modules.util.EmbedUtil;
import edu.ksu.canvas.CanvasApiFactory;
import edu.ksu.canvas.interfaces.DiscussionTopicReader;
import edu.ksu.canvas.model.DiscussionTopic;
import edu.ksu.canvas.model.File;
import edu.ksu.canvas.requestOptions.GetSingleDiscussionTopicOptions;
import io.github.furstenheim.CopyDown;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.io.IOException;
import java.util.List;

@DiscordCommand(name = "canvas-discussion", description = "Show an announcement for a course on Canvas", guildOnly = false)
public class CanvasDiscussionCommand implements CanvasCommand {

    CopyDown converter = new CopyDown();

    @Override
    public void canvasCommand(SlashCommandInteractionEvent event, DiscordCanvasUser user, boolean ephemeral) throws IOException {
        event.deferReply(ephemeral).queue();

        String courseId = event.getOption("course").getAsString();
        String discussionId = event.getOption("discussion").getAsString();

        Institution institution = user.getInstitution();
        CanvasApiFactory factory = CanvasFactoryManager.getFactory(institution);

        DiscussionTopicReader reader = factory.getReader(DiscussionTopicReader.class, user.getOauth());
        DiscussionTopic topic = reader.getDiscussionTopic(new GetSingleDiscussionTopicOptions(courseId, discussionId, GetSingleDiscussionTopicOptions.IdType.COURSES)).orElseThrow();


        EmbedBuilder embedBuilder = institution.getEmbed().setThumbnail(null);
        embedBuilder.setTitle(topic.getTitle());

        String content = converter.convert(topic.getMessage());
        if (content.length() > EmbedUtil.MAX_DESCRIPTION_LENGTH) {
            EmbedUtil.sendLongDescriptionEmbed(embedBuilder, content, event.getChannel(), institution.getCanvasLogoFileUpload());
            event.getHook().sendMessage("The content was too long to send in one message. It has been split into multiple messages.").setEphemeral(ephemeral).queue();
        } else {
            embedBuilder.setDescription(content);
            event.getHook().sendMessageEmbeds(embedBuilder.build()).addFiles(institution.getCanvasLogoFileUpload()).setEphemeral(ephemeral).queue();
        }
        System.out.println("attachment: " + topic.getAttachments().stream().map(File::getDisplayName).toList());
    }

    @Override
    public List<OptionData> addOptions() {
        return List.of(
                new OptionData(OptionType.STRING, "course", "The course ID", true),
                new OptionData(OptionType.STRING, "discussion", "The discussion ID", true)
        );
    }
}
