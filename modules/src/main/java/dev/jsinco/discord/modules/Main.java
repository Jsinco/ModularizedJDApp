package dev.jsinco.discord.modules;

import dev.jsinco.abstractjavafilelib.FileLibSettings;
import dev.jsinco.discord.framework.AbstractModule;
import dev.jsinco.discord.framework.FrameWork;
import dev.jsinco.abstractjavafilelib.schemas.SnakeYamlConfig;
import dev.jsinco.discord.framework.settings.Settings;
import lombok.Getter;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Main Modules class. Entry point for our app and calls our framework to
 * connect to Discord and register modules that cannot be registered through reflection.
 * @since 1.0
 * @see FrameWork
 * @see AbstractModule
 * @author Jonah
 */
public class Main {

    @Getter private static Settings settings;
    @Getter private static SnakeYamlConfig savesFile;


    public static void main(String[] args) {
        // Needs to be initialized before the framework starts

        // Optional custom data folder stuff. In the future, we can use more than just these simple flat files from my lib.
        Path dataFolderPath = setupDataFolder();
        System.out.println("Using " + dataFolderPath + " as data folder.");
        savesFile = new SnakeYamlConfig("saves.yml");


        // Start the framework
        FrameWork.start(Main.class, dataFolderPath);

        // Needs to be initialized after the framework starts
        settings = FrameWork.getFileManager().getSettings();
    }


    private static Path setupDataFolder() {
        String newDataFolderPath = System.getProperty("dataFolder");
        if (newDataFolderPath == null) {
            newDataFolderPath = System.getenv("dataFolder");
        }

        if (newDataFolderPath != null) {
            File newFolder = new File(newDataFolderPath);

            if (!newFolder.exists()) {
                newFolder.mkdirs();
                System.out.println("Created new data folder at " + newFolder.getPath());
            }

            if (!newFolder.isDirectory()) {
                System.out.println("Provided data folder does is not a directory. Using default data folder.");
            } else {
                FileLibSettings.set(newFolder);
            }
        }
        return FileLibSettings.getDataFolder().toPath();
    }

}