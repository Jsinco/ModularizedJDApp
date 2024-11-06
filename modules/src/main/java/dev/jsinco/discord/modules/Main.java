package dev.jsinco.discord.modules;

import dev.jsinco.discord.framework.AbstractModule;
import dev.jsinco.discord.framework.FrameWork;
import dev.jsinco.discord.modules.util.Util;

import java.io.File;
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
        String newDataFolderPath = Util.getFromEnvironment("dataFolder");
        File folder = getDefaultDataFolder();

        if (newDataFolderPath != null) {
            folder = new File(newDataFolderPath);

            if (!folder.exists()) {
                folder.mkdirs();
                System.out.println("Created new data folder at " + folder.getPath() + ".");
            }

            if (!folder.isDirectory()) {
                System.out.println("Provided data folder is not a directory. Using default data folder instead.");
            }
        }
        return folder.toPath();
    }

    private static File getDefaultDataFolder() {
        String path = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        File jarFile = new File(path);
        String jarDir = jarFile.getParentFile().getAbsolutePath();

        File dataFolder = new File(jarDir + File.separator + "data");
        dataFolder.mkdirs();

        return dataFolder;
    }

}