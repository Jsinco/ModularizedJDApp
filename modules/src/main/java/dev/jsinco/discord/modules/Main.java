package dev.jsinco.discord.modules;

import dev.jsinco.abstractjavafilelib.FileLibSettings;
import dev.jsinco.discord.framework.AbstractModule;
import dev.jsinco.discord.framework.FrameWork;
import dev.jsinco.abstractjavafilelib.schemas.SnakeYamlConfig;
import lombok.Getter;

import java.io.*;
import java.nio.file.Path;

/**
 * Main Modules class. Entry point for our app and calls our framework to
 * connect to Discord and register modules that cannot be registered through reflection.
 * @since 1.0
 * @see FrameWork
 * @see AbstractModule
 * @author Jonah
 */
public class Main {



    public static void main(String[] args) {
        // Optional custom data folder stuff. In the future, we can use more than just these simple flat files from my lib.
        Path dataFolderPath = setupDataFolder();
        System.out.println("Using " + dataFolderPath + " as data folder.");

        // Start the framework
        FrameWork.start(Main.class, dataFolderPath);
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
                System.out.println("Created new data folder at " + newFolder.getPath() + ".");
            }

            if (!newFolder.isDirectory()) {
                System.out.println("Provided data folder is not a directory. Using default data folder instead.");
            } else {
                FileLibSettings.set(newFolder);
            }
        }
        return FileLibSettings.getDataFolder().toPath();
    }

}