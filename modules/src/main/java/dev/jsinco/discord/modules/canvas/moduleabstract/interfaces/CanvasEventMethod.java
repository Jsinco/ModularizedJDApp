package dev.jsinco.discord.modules.canvas.moduleabstract.interfaces;

import edu.ksu.canvas.interfaces.CanvasReader;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CanvasEventMethod {
    Class<? extends CanvasReader> value();
}
