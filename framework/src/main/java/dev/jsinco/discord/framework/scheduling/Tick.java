package dev.jsinco.discord.framework.scheduling;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Tick {
    TimeUnit unit() default TimeUnit.MILLISECONDS;
    long delay() default 0L;
    long period();
}
