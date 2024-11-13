package dev.jsinco.discord.modules.canvas.commands;

import dev.jsinco.discord.framework.commands.DiscordCommand;
import dev.jsinco.discord.modules.canvas.CanvasFactoryManager;
import dev.jsinco.discord.modules.canvas.encapsulation.DiscordCanvasUser;
import dev.jsinco.discord.modules.canvas.moduleabstract.interfaces.CanvasModule;
import dev.jsinco.discord.modules.util.EmbedUtil;
import edu.ksu.canvas.CanvasApiFactory;
import edu.ksu.canvas.interfaces.AssignmentReader;
import edu.ksu.canvas.interfaces.CourseReader;
import edu.ksu.canvas.model.assignment.Assignment;
import edu.ksu.canvas.requestOptions.ListCourseAssignmentsOptions;
import edu.ksu.canvas.requestOptions.ListCurrentUserCoursesOptions;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction;

import java.io.IOException;
import java.util.List;

public class CanvasCourseAssignmentsCommand implements CanvasModule {

    private static final String MODULE_STR = "canvas-assignments";

    @DiscordCommand(name = MODULE_STR, description = "Show information about your Canvas course assignments", guildOnly = false)
    @Override
    public void canvasCommand(SlashCommandInteractionEvent event, DiscordCanvasUser canvasUser, boolean ephemeral) throws Exception {
        event.deferReply(ephemeral).queue();

        CanvasApiFactory factory = CanvasFactoryManager.getFactory(canvasUser.getInstitution());
        CourseReader courseReader = factory.getReader(CourseReader.class, canvasUser.getOauth());


        EmbedBuilder embedBuilder = canvasUser.getInstitution().getEmbed();
        embedBuilder.setTitle("Pick a course to view assignments");
        embedBuilder.setDescription("Select a course to view assignments for");

        List<Button> buttons =  courseReader.listCurrentUserCourses(new ListCurrentUserCoursesOptions()).stream().map(course ->
                Button.of(ButtonStyle.SUCCESS, MODULE_STR + ";" + event.getUser().getId() + ";" + course.getId() + ";" + ephemeral, course.getName() + " (" + course.getId() + ")")).toList();

        WebhookMessageCreateAction<Message> msg = event.getHook().sendMessageEmbeds(embedBuilder.build())
                .addFiles(canvasUser.getInstitution().getCanvasLogoFileUpload())
                .setEphemeral(ephemeral);

        for (int i = 0; i < Math.min(buttons.size(), 25); i++) {
            if (i % 5 == 0) {
                msg = msg.addActionRow(buttons.subList(i, Math.min(i + 5, buttons.size())));
            }
        }

        msg.queue();
    }

    @SubscribeEvent
    public void onButtonInteract(ButtonInteractionEvent event) {
        if (!event.getComponentId().startsWith(MODULE_STR)) {
            return;
        }


        String[] split = event.getComponentId().split(";");
        String courseId = split[2];
        boolean ephemeral = Boolean.parseBoolean(split[3]);

        if (!event.getUser().getId().equals(split[1])) {
            event.reply("You cannot use this button").setEphemeral(true).queue();
            return;
        }

        event.deferReply(ephemeral).queue();

        DiscordCanvasUser user = DiscordCanvasUser.from(event.getUser());

        if (user == null) {
            event.getHook().sendMessage("You must link your Canvas account before using this.").setEphemeral(ephemeral).queue();
            return;
        }

        CanvasApiFactory factory = CanvasFactoryManager.getFactory(user.getInstitution());
        AssignmentReader assignmentReader = factory.getReader(AssignmentReader.class, user.getOauth());

        StringBuilder sb = new StringBuilder();

        List<Assignment> assignments;
        try {
            assignments = assignmentReader.listCourseAssignments(new ListCourseAssignmentsOptions(courseId));
        } catch (IOException e) {
            e.printStackTrace(); // TODO: add some event error handling
            return;
        }

        assignments.forEach(it -> sb.append(it.getName()).append(" (Due date: " + it.getDueAt() + ")").append("\n"));

        EmbedBuilder embedBuilder = user.getInstitution().getEmbed();
        embedBuilder.setTitle("Assignments for course " + courseId);
        List<MessageEmbed> embeds = EmbedUtil.getLongDescriptionEmbeds(embedBuilder, sb.toString());
        event.getHook().sendMessageEmbeds(embeds).addFiles(user.getInstitution().getCanvasLogoFileUpload()).setEphemeral(ephemeral).queue();
    }
}
