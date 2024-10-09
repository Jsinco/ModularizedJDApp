package dev.jsinco.discord.framework.commands;

import net.dv8tion.jda.api.Permission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Info for a Discord command.
 * Should be placed on either class or method with a class that implements CommandModule.
 * @see CommandModule
 * @see CommandManager
 * @see CommandOption
 * @since 1.0
 * @author Jonah
 */
@Target({ElementType.TYPE, ElementType.METHOD}) // Can go on execute func or class
@Retention(RetentionPolicy.RUNTIME)
public @interface DiscordCommand {

    /**
     * The name of the command.
     * @return the name of the command.
     */
    String name();

    /**
     * The description of the command.
     * @return the description of the command.
     */
    String description() default "";

    /**
     * The permission level required to execute this command.
     * @return the permission level required to execute this command.
     */
    Permission permission() default Permission.UNKNOWN;

    /**
     * Whether this command should only be available in guilds.
     * @return true if this command should only be available in guilds, false otherwise.
     */
    boolean guildOnly() default true;
}
