package dev.jsinco.discord.framework.scheduling;

import dev.jsinco.discord.framework.logging.FrameWorkLogger;
import lombok.Getter;

import java.util.TimerTask;

@Getter
public abstract class Tickable extends TimerTask {

    private final long delay;
    private final long period;

    public Tickable() {
        Tick annotation = getClass().getAnnotation(Tick.class);
        if (annotation == null) {
            try {
                annotation = getClass().getMethod("onTick").getAnnotation(Tick.class);
            } catch (NoSuchMethodException ignored) {
                FrameWorkLogger.error("Unable to find @Tick annotation on class or method! All tickables should have a @Tick annotation!", ignored);
            }
        }
        TimeUnit unit = annotation != null ? annotation.unit() : TimeUnit.MILLISECONDS;


        this.delay = annotation != null ? unit.toMillis(annotation.delay()) : 0L;
        this.period = annotation != null ? unit.toMillis(annotation.period()) : 1000L;
    }

    // Just renaming the method to onTick

    @Override
    public void run() {
        this.onTick();
    }

    public abstract void onTick();

}
