package dev.jsinco.discord.modules.moduleimpl.canvas.moduleabstract.impl;

import dev.jsinco.discord.framework.logging.FrameWorkLogger;
import dev.jsinco.discord.framework.scheduling.TimeUnit;
import dev.jsinco.discord.modules.moduleimpl.canvas.CanvasFactoryManager;
import dev.jsinco.discord.modules.moduleimpl.canvas.DiscordCanvasUser;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CanvasCourseAnnouncementEventImpl extends BaseEventImpl {

    // Read announcements.
    // UserID and AnnouncementID
    private final Map<Long, List<Long>> readAnnouncements = new HashMap<>();

    public CanvasCourseAnnouncementEventImpl(TimeUnit timeUnit, long delay, long period) {
        super(timeUnit, delay, period);
    }

    @Override
    public void tickEvent(DiscordCanvasUser user) throws IOException {
        long start = System.currentTimeMillis();
        CanvasApiFactory factory = CanvasFactoryManager.getFactory(user.getInstitution());
        List<Course> courses = factory.getReader(CourseReader.class, user.getOauth()).listCurrentUserCourses(new ListCurrentUserCoursesOptions());
        DiscussionTopicReader reader = factory.getReader(DiscussionTopicReader.class, user.getOauth());

        OUTER_LOOP: for (Course course : courses) {
            List<DiscussionTopic> topics = reader.listDiscussionTopics(
                    new ListDiscussionTopicsOptions(String.valueOf(course.getId()), ListDiscussionTopicsOptions.IdType.COURSES)
                            .onlyAnnouncements()
            );

            for (DiscussionTopic topic : topics) {
                // Do something with the announcement
                if (topic.getPostedAt() == null) {
                    continue;
                }
                LocalDate topicDate = topic.getPostedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate currentDate = LocalDate.now();

                if (!topicDate.equals(currentDate) || readAnnouncements.getOrDefault(user.getUser().getIdLong(), new ArrayList<>()).contains(topic.getId())) {
                    continue;
                }

                readAnnouncements.computeIfAbsent(user.getUser().getIdLong(), k -> new ArrayList<>()).add(topic.getId());
                this.dispatchEvent(DiscussionTopicReader.class, user, topic);
                break OUTER_LOOP;
            }
        }

        FrameWorkLogger.info("Finished CanvasCourseAnnouncementEventImpl tickEvent in " + (System.currentTimeMillis() - start) + "ms for " + user.getUser().getName());
    }
}
