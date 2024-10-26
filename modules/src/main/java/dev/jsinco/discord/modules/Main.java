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
        // Optional custom data folder stuff. In the future, we can use more than just these simple flat files from my lib.
        String newDataFolderPath = System.getProperty("dataFolder");
        if (newDataFolderPath == null) {
            newDataFolderPath = System.getenv("dataFolder");
        }

        if (newDataFolderPath != null) {
            File newFolder = new File(newDataFolderPath);
            if (newFolder.exists() && newFolder.isDirectory()) {
                FileLibSettings.set(newFolder);
            } else {
                System.out.println("Provided data folder does not exist or is not a directory. Using default data folder.");
            }
        }
        System.out.println("Using " + FileLibSettings.getDataFolder().getPath() + " as data folder.");
        SAVES_FILE = new SnakeYamlConfig("saves.yml"); // Init our main persistent data file.

        // Start the framework
        FrameWork.start(Main.class);
    }

}