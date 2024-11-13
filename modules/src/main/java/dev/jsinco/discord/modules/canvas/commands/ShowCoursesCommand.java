package dev.jsinco.discord.modules.canvas.commands;

import dev.jsinco.discord.framework.commands.DiscordCommand;
import dev.jsinco.discord.modules.canvas.encapsulation.DiscordCanvasUser;
import dev.jsinco.discord.modules.canvas.encapsulation.institute.Institution;
import dev.jsinco.discord.modules.canvas.CanvasFactoryManager;
import dev.jsinco.discord.modules.canvas.moduleabstract.interfaces.CanvasCommand;
import edu.ksu.canvas.CanvasApiFactory;
import edu.ksu.canvas.interfaces.CourseReader;
import edu.ksu.canvas.model.Course;
import edu.ksu.canvas.requestOptions.ListCurrentUserCoursesOptions;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.io.IOException;
import java.util.List;

@DiscordCommand(name = "canvas-courses", description = "Show the courses you are enrolled in on Canvas", guildOnly = false)
public class ShowCoursesCommand implements CanvasCommand {
    @Override
    public void canvasCommand(SlashCommandInteractionEvent event, DiscordCanvasUser user, boolean ephemeral) throws IOException {
        event.deferReply(ephemeral).queue();

        CanvasApiFactory factory = CanvasFactoryManager.getFactory(user.getInstitution());
        CourseReader courseReader = factory.getReader(CourseReader.class, user.getOauth());

        List<Course> courses = courseReader.listCurrentUserCourses(new ListCurrentUserCoursesOptions());
        StringBuilder response = new StringBuilder("**Your Courses:**\n");
        for (Course course : courses) {
            response.append("  * ").append(course.getName()).append(" (").append(course.getId()).append(")\n");
        }

        Institution institution = user.getInstitution();
        event.getHook().sendMessageEmbeds(institution.getEmbed().setDescription(response.toString()).build())
                .addFiles(institution.getCanvasLogoFileUpload())
                .setEphemeral(ephemeral).queue();
    }
}
