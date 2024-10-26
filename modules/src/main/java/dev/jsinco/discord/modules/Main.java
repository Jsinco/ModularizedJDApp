package dev.jsinco.discord.modules;

import dev.jsinco.abstractjavafilelib.FileLibSettings;
import dev.jsinco.discord.framework.AbstractModule;
import dev.jsinco.discord.framework.FrameWork;
import dev.jsinco.discord.framework.reflect.InjectStatic;
import dev.jsinco.abstractjavafilelib.schemas.SnakeYamlConfig;
import net.dv8tion.jda.api.JDA;

import java.io.File;

/**
 * Main Modules class. Entry point for our app and calls our framework to
 * connect to Discord and register modules that cannot be registered through reflection.
 * @since 1.0
 * @see FrameWork
 * @see AbstractModule
 * @author Jonah
 */
public class Main {

    @InjectStatic(from = FrameWork.class)
    private static JDA jda;
    private static SnakeYamlConfig SAVES_FILE;


    public static void main(String[] args) {
        FileLibSettings.set(new File("C:\\Users\\jonah\\idea\\ModularizedJDApp\\testing"));
        SAVES_FILE = new SnakeYamlConfig("saves.yml");
        FrameWork.start(Main.class);
    }

}