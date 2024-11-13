package dev.jsinco.discord.modules.canvas.moduleabstract.impl;

import dev.jsinco.discord.framework.scheduling.TimeUnit;
import dev.jsinco.discord.modules.canvas.encapsulation.DiscordCanvasUser;
import dev.jsinco.discord.modules.canvas.encapsulation.DiscordCanvasUserData;
import edu.ksu.canvas.CanvasApiFactory;
import edu.ksu.canvas.interfaces.CourseReader;
import edu.ksu.canvas.interfaces.DiscussionTopicReader;
import edu.ksu.canvas.model.Course;
import edu.ksu.canvas.model.DiscussionTopic;
import edu.ksu.canvas.requestOptions.ListCurrentUserCoursesOptions;
import edu.ksu.canvas.requestOptions.ListDiscussionTopicsOptions;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

public class CanvasCourseAnnouncementEventImpl extends BaseEventImpl {


    public CanvasCourseAnnouncementEventImpl(TimeUnit timeUnit, long delay, long period) {
        super(timeUnit, delay, period);
    }

    @Override
    public void onEventCall(DiscordCanvasUser user, CanvasApiFactory factory) throws IOException {
        DiscordCanvasUserData userData = user.getUserData();
        if (!userData.isNotifications())  {
            return;
        }


        List<Course> courses = factory.getReader(CourseReader.class, user.getOauth()).listCurrentUserCourses(new ListCurrentUserCoursesOptions());
        DiscussionTopicReader reader = factory.getReader(DiscussionTopicReader.class, user.getOauth());

        for (Course course : courses) {
            List<DiscussionTopic> topics = reader.listDiscussionTopics(
                    new ListDiscussionTopicsOptions(String.valueOf(course.getId()), ListDiscussionTopicsOptions.IdType.COURSES)
                            .onlyAnnouncements()
            );

            for (DiscussionTopic topic : topics) {
                if (topic.getPostedAt() == null) {
                    continue;
                }
                LocalDate topicDate = topic.getPostedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                if (!topicDate.equals(LocalDate.now()) || userData.getNotifiedDiscussions().contains(topic.getId())) {
                    continue;
                }

                userData.getNotifiedDiscussions().add(topic.getId());
                this.dispatchEvent(DiscussionTopicReader.class, user, topic, course);
            }
        }
    }
}
