package dev.jsinco.discord.modules.moduleimpl.canvas.events;

import dev.jsinco.discord.modules.moduleimpl.canvas.DiscordCanvasUser;
import dev.jsinco.discord.modules.moduleimpl.canvas.moduleabstract.interfaces.CanvasEventMethod;
import dev.jsinco.discord.modules.moduleimpl.canvas.moduleabstract.interfaces.CanvasLMSEvent;
import edu.ksu.canvas.interfaces.DiscussionTopicReader;
import edu.ksu.canvas.model.DiscussionTopic;
import edu.ksu.canvas.model.File;
import io.github.furstenheim.CopyDown;
import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;

@Getter
public class CanvasLMSListeners implements CanvasLMSEvent {

    CopyDown converter = new CopyDown();

    @CanvasEventMethod(DiscussionTopicReader.class)
    public void onCanvasLMSAnnouncementReceived(DiscordCanvasUser canvasUser, DiscussionTopic announcement) {
        User jdaEntity = canvasUser.getUser();

        EmbedBuilder embedBuilder = canvasUser.getInstitution().getEmbed().setThumbnail(null);


        embedBuilder.setTitle(announcement.getTitle());
        embedBuilder.setDescription(converter.convert(announcement.getMessage()));
        embedBuilder.setFooter("Post: " + announcement.getHtmlUrl());
        System.out.println("attachment: " + announcement.getAttachments().stream().map(File::getDisplayName).toList());

        jdaEntity.openPrivateChannel().complete().sendMessageEmbeds(embedBuilder.build()).addFiles(canvasUser.getInstitution().getCanvasLogoFileUpload()).queue();
        System.out.println("Received announcementTSADFFDSFD: " + announcement.getTitle());
    }
}
