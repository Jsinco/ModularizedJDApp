package dev.jsinco.discord.modules.moduleimpl.canvas.commands;

import dev.jsinco.discord.framework.commands.CommandModule;
import dev.jsinco.discord.framework.commands.DiscordCommand;
import dev.jsinco.discord.modules.moduleimpl.canvas.CanvasFactoryManager;
import dev.jsinco.discord.modules.moduleimpl.canvas.DiscordCanvasUser;
import dev.jsinco.discord.modules.moduleimpl.canvas.DiscordCanvasUserManager;
import edu.ksu.canvas.CanvasApiFactory;
import edu.ksu.canvas.interfaces.AssignmentReader;
import edu.ksu.canvas.interfaces.CourseReader;
import edu.ksu.canvas.model.Course;
import edu.ksu.canvas.requestOptions.ListCourseAssignmentsOptions;
import edu.ksu.canvas.requestOptions.ListCurrentUserCoursesOptions;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.List;

public class CanvasAssignmentsInfoCommand implements CommandModule {
    @DiscordCommand(name = "account", description = "Show information about your Canvas account")
    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception {
        DiscordCanvasUser user = DiscordCanvasUserManager.getLinkedAccount(event.getUser().getId());
        if (user == null) {
            event.reply("You must link your Canvas account before using this command.").setEphemeral(true).queue();
            return;
        }

        event.deferReply(true).queue();

        CanvasApiFactory factory = CanvasFactoryManager.getFactory(user.getInstitution());

        CourseReader courseReader = factory.getReader(CourseReader.class, user.getOauth());

        List<Course> courses = courseReader.listCurrentUserCourses(new ListCurrentUserCoursesOptions());

        EmbedBuilder embedBuilder = new EmbedBuilder();

        //        AssignmentReader assignmentReader = factory.getReader(AssignmentReader.class, user.getOauth());
//        assignmentReader.listCourseAssignments(new ListCourseAssignmentsOptions("141571"))
//                        .forEach(it -> sb.append(it.getName()).append("\n"));
    }
}
