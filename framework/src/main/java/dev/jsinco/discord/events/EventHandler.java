package dev.jsinco.discord.events;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EventHandler {
    // If priority is needed some day, I'll add it
    //EventPriority priority() default EventPriority.NORMAL;
}
