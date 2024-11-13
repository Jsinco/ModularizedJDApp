package dev.jsinco.discord.modules.canvas;

import dev.jsinco.discord.modules.canvas.encapsulation.DiscordCanvasUser;
import dev.jsinco.discord.modules.canvas.encapsulation.institute.Institution;
import dev.jsinco.discord.modules.canvas.moduleabstract.interfaces.CanvasEventMethod;
import dev.jsinco.discord.modules.canvas.moduleabstract.interfaces.CanvasEvent;
import dev.jsinco.discord.modules.util.CanvasUtil;
import dev.jsinco.discord.modules.util.EmbedUtil;
import dev.jsinco.discord.modules.util.ImageUtil;
import dev.jsinco.discord.modules.util.StringUtil;
import edu.ksu.canvas.interfaces.AssignmentReader;
import edu.ksu.canvas.interfaces.DiscussionTopicReader;
import edu.ksu.canvas.model.Course;
import edu.ksu.canvas.model.DiscussionTopic;
import edu.ksu.canvas.model.assignment.Assignment;
import io.github.furstenheim.CopyDown;
import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Getter
public class CanvasListeners implements CanvasEvent {

    private final CopyDown converter = new CopyDown();


    @CanvasEventMethod(AssignmentReader.class)
    public void onCanvasLMSAssignmentDueToday(DiscordCanvasUser canvasUser, Assignment assignment, Course course) {
        User jdaEntity = canvasUser.getUser();

        EmbedBuilder embedBuilder = canvasUser.getInstitution().getEmbed();

        LocalDateTime dateTime = assignment.getDueAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        String amPmDueTime = StringUtil.convertToAmPm(dateTime.toLocalTime().toString());

        embedBuilder.setTitle("You Have an Assignment Due at " + amPmDueTime + " Today");
        embedBuilder.setDescription("""
                **Assignment Name:** %s
                **Due Date:** %s
                **Due Time:** %s
                **Worth:** %s points
                """.formatted(assignment.getName(), dateTime.toLocalDate(), amPmDueTime, assignment.getPointsPossible()));

        if (assignment.getLockAt() != null) {
            embedBuilder.addField("Lock Date", assignment.getLockAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().toString(), true);
        }
        embedBuilder.addField("Assignment", "[Click to View Assignment]("+ assignment.getHtmlUrl() + ")", true);
        embedBuilder.addField("Course","**" + course.getName() + "**" , true);


        embedBuilder.setThumbnail(CanvasUtil.getCanvasLogoUrl(String.valueOf(course.getId())));

        jdaEntity.openPrivateChannel().complete().sendMessageEmbeds(embedBuilder.build())
                .addFiles(canvasUser.getInstitution().getCanvasLogoFileUpload())
                .addFiles(CanvasUtil.getCanvasLogoFileUpload(ImageUtil.generateColor(course.getId()), String.valueOf(course.getId())))
                .addActionRow(
                        Button.of(ButtonStyle.DANGER, "canvas-notifications", "Disable Canvas LMS Notifications", Emoji.fromUnicode("U+1F6D1"))
                )
                .queue();
    }


    @CanvasEventMethod(DiscussionTopicReader.class)
    public void onCanvasLMSAnnouncementReceived(DiscordCanvasUser canvasUser, DiscussionTopic announcement, Course course) {
        User jdaEntity = canvasUser.getUser();

        EmbedBuilder embedBuilder = canvasUser.getInstitution().getEmbed().setThumbnail(null);


        embedBuilder.setTitle(announcement.getTitle());
        embedBuilder.setDescription(StringUtil.cutOffString(converter.convert(announcement.getMessage()), EmbedUtil.MAX_DESCRIPTION_LENGTH));
        embedBuilder.addField("Post", "[Click to view Announcement]("+ announcement.getHtmlUrl() + ")", true);
        embedBuilder.addField("Author", "**" + announcement.getUserName() + "**", true);
        embedBuilder.addField("Course","**" + course.getName() + "**" , true);

        jdaEntity.openPrivateChannel().complete().sendMessageEmbeds(embedBuilder.build())
                .addFiles(canvasUser.getInstitution().getCanvasLogoFileUpload())
                .addActionRow(
                        Button.of(ButtonStyle.DANGER, "canvas-notifications", "Disable Canvas LMS Notifications", Emoji.fromUnicode("U+1F6D1"))
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
            canvasUser.getUserData().setNotifications(!canvasUser.getUserData().isNotifications());

            embedBuilder.setTitle("Canvas LMS Notifications");
            embedBuilder.setDescription("Canvas LMS Notifications are now " + (canvasUser.getUserData().isNotifications() ? "enabled" : "disabled"));
            event.replyEmbeds(embedBuilder.build()).addFiles(canvasUser.getInstitution().getCanvasLogoFileUpload()).queue();
        }
    }
}
