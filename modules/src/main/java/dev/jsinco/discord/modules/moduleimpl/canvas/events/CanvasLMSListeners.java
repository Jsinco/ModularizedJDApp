package dev.jsinco.discord.modules.moduleimpl.canvas.events;

import dev.jsinco.discord.modules.moduleimpl.canvas.encapsulation.DiscordCanvasUser;
import dev.jsinco.discord.modules.moduleimpl.canvas.encapsulation.Institution;
import dev.jsinco.discord.modules.moduleimpl.canvas.moduleabstract.interfaces.CanvasEventMethod;
import dev.jsinco.discord.modules.moduleimpl.canvas.moduleabstract.interfaces.CanvasLMSEvent;
import dev.jsinco.discord.modules.util.EmbedUtil;
import dev.jsinco.discord.modules.util.Util;
import edu.ksu.canvas.interfaces.DiscussionTopicReader;
import edu.ksu.canvas.model.Course;
import edu.ksu.canvas.model.DiscussionTopic;
import io.github.furstenheim.CopyDown;
import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

@Getter
public class CanvasLMSListeners implements CanvasLMSEvent {

    CopyDown converter = new CopyDown();

    @CanvasEventMethod(DiscussionTopicReader.class)
    public void onCanvasLMSAnnouncementReceived(DiscordCanvasUser canvasUser, DiscussionTopic announcement, Course course) {
        User jdaEntity = canvasUser.getUser();

        EmbedBuilder embedBuilder = canvasUser.getInstitution().getEmbed().setThumbnail(null);


        embedBuilder.setTitle(announcement.getTitle());
        embedBuilder.setDescription(Util.cutOffString(converter.convert(announcement.getMessage()), EmbedUtil.MAX_DESCRIPTION_LENGTH));
        embedBuilder.addField("Post", "[Canvas Announcement Link]("+ announcement.getHtmlUrl() + ")", true);
        embedBuilder.addField("Author", "**" + announcement.getUserName() + "**", true);
        embedBuilder.addField("Course","**" + course.getName() + "**" , true);

        jdaEntity.openPrivateChannel().complete().sendMessageEmbeds(embedBuilder.build())
                .addFiles(canvasUser.getInstitution().getCanvasLogoFileUpload())
                .addActionRow(
                        Button.of(ButtonStyle.DANGER, "canvas-notifications", "Disable Canvas LMS Notifications")
                )
                .queue();
    }


    @SubscribeEvent
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (!event.getComponentId().equals("canvas-notifications")) {
            return;
        }

        DiscordCanvasUser canvasUser = DiscordCanvasUser.from(event.getUser());
        EmbedBuilder embedBuilder = Institution.UNKNOWN_INSTITUTION.getEmbed();

        if (canvasUser == null) {
            embedBuilder.setTitle("Canvas LMS Notifications");
            embedBuilder.setDescription("You have not linked your Canvas account.");
            event.replyEmbeds(embedBuilder.build()).addFiles(Institution.UNKNOWN_INSTITUTION.getCanvasLogoFileUpload()).queue();
        } else {
            embedBuilder = canvasUser.getInstitution().getEmbed();
            canvasUser.setNotifications(!canvasUser.isNotifications());

            embedBuilder.setTitle("Canvas LMS Notifications");
            embedBuilder.setDescription("Canvas LMS Notifications are now " + (canvasUser.isNotifications() ? "enabled" : "disabled"));
            event.replyEmbeds(embedBuilder.build()).addFiles(canvasUser.getInstitution().getCanvasLogoFileUpload()).queue();
        }
    }
}
