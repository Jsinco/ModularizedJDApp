package dev.jsinco.discord.modules;

import dev.jsinco.abstractjavafilelib.schemas.SnakeYamlConfig;
import dev.jsinco.discord.framework.AbstractModule;
import dev.jsinco.discord.framework.FrameWork;
import dev.jsinco.discord.framework.commands.CommandManager;
import dev.jsinco.discord.framework.commands.CommandModule;
import dev.jsinco.discord.framework.reflect.InjectStatic;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

import java.util.Map;

/**
 * Main Modules class. Entry point for our app and calls our framework to
 * connect to Discord and register modules that can be registered through reflection.
 * @since 1.0
 * @see FrameWork
 * @see AbstractModule
 * @author Jonah
 */
public class Main {

    @InjectStatic(from = FrameWork.class)
    private static JDA jda;
    private static final SnakeYamlConfig saves = new SnakeYamlConfig("saves.yml");


    public static void main(String[] args) {
        FrameWork.start(Main.class);
    }

}