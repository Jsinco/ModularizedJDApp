package dev.jsinco.discord.modules.moduleimpl.canvas.moduleabstract.impl;

import dev.jsinco.discord.framework.scheduling.TimeUnit;
import dev.jsinco.discord.modules.moduleimpl.canvas.encapsulation.DiscordCanvasUser;
import dev.jsinco.discord.modules.moduleimpl.canvas.encapsulation.DiscordCanvasUserData;
import edu.ksu.canvas.CanvasApiFactory;
import edu.ksu.canvas.interfaces.AssignmentReader;
import edu.ksu.canvas.interfaces.CourseReader;
import edu.ksu.canvas.model.Course;
import edu.ksu.canvas.model.assignment.Assignment;
import edu.ksu.canvas.requestOptions.ListCourseAssignmentsOptions;
import edu.ksu.canvas.requestOptions.ListCurrentUserCoursesOptions;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

public class CanvasAssignmentDueEventImpl extends BaseEventImpl {


    public CanvasAssignmentDueEventImpl(TimeUnit timeUnit, long delay, long period) {
        super(timeUnit, delay, period);
    }

    @Override
    public void onEventCall(DiscordCanvasUser user, CanvasApiFactory factory) throws IOException {
        DiscordCanvasUserData userData = user.getUserData();
        if (!userData.isNotifications())  {
            return;
        }

        List<Course> courses = factory.getReader(CourseReader.class, user.getOauth()).listCurrentUserCourses(new ListCurrentUserCoursesOptions());
        AssignmentReader reader = factory.getReader(AssignmentReader.class, user.getOauth());

        for (Course course : courses) {
            List<Assignment> assignments = reader.listCourseAssignments(new ListCourseAssignmentsOptions(String.valueOf(course.getId())));

            for (Assignment assignment : assignments) {
                if (assignment.getDueAt() == null) {
                    continue;
                }
                LocalDate topicDate = assignment.getDueAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate currentDate = LocalDate.now();


                if (!topicDate.equals(currentDate) || userData.getNotifiedAssignments().contains(assignment.getId())) {
                    continue;
                }

                userData.getNotifiedAssignments().add(assignment.getId());
                this.dispatchEvent(AssignmentReader.class, user, assignment, course);
            }
        }
    }
}
