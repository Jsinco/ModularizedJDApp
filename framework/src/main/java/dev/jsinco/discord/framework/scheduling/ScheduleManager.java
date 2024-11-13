package dev.jsinco.discord.framework.scheduling;

import dev.jsinco.discord.framework.FrameWork;
import dev.jsinco.discord.framework.logging.FrameWorkLogger;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@Getter
public class ScheduleManager extends Timer {

    private static ScheduleManager instance;
    public static ScheduleManager getInstance() {
        if (instance == null) {
            instance = new ScheduleManager();
        }
        return instance;
    }

    private final List<Tickable> scheduledTasks = new ArrayList<>();

    private ScheduleManager() {
        super(FrameWork.getCaller().getSimpleName().toLowerCase() + "-scheduler");
    }


    public Tickable schedule(Tickable tickable) {
        System.out.println("Scheduling " + tickable.getClass().getSimpleName());
        super.schedule(tickable, tickable.getDelay(), tickable.getPeriod());
        scheduledTasks.add(tickable);
        return tickable;
    }

    public void schedule(TimerTask task, long delay, long period) {
        super.schedule(task, delay, period);
        if (task instanceof Tickable) {
            scheduledTasks.add((Tickable) task);
        }
    }
}
