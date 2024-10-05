package dev.jsinco.discord.utility;

import dev.jsinco.discord.logging.FrameWorkLogger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public final class ReflectionUtil {

    public static final String BASE_PACKAGE = "dev.jsinco.discord";

    public static List<Class<?>> getAllClassesFor(Class<?> clazz) {

        try {
            // Get the URL of the package and its directory structure
            URL packageUrl = ReflectionUtil.class.getClassLoader().getResource(BASE_PACKAGE.replace(".", "/"));

            if (packageUrl != null) {
                // Recursively find all listener classes
                return findListenerClassesInPackage(BASE_PACKAGE, packageUrl, clazz);
            }
        } catch (IOException e) {
            FrameWorkLogger.error("An error occurred while searching for classes!", e);
        }

        return List.of();
    }

    private static List<Class<?>> findListenerClassesInPackage(String packageName, URL packageUrl, Class<?> classToSearchFor) throws IOException {
        // Convert the URL into a directory path
        File directory = new File(packageUrl.getFile());

        // If the directory exists, process its contents
        if (directory.exists()) {
            // Get all files in this directory (including subdirectories)
            File[] files = directory.listFiles();

            if (files == null) {
                return List.of();
            }
            List<Class<?>> listenerClasses = new ArrayList<>();
            for (File file : files) {
                if (file.isDirectory()) {
                    // If it's a directory, recurse into it
                    listenerClasses.addAll(findListenerClassesInPackage(packageName + "." + file.getName(), file.toURI().toURL(), classToSearchFor));
                } else if (file.getName().endsWith(".class")) {
                    // If it's a .class file, check if it's a listener class
                    String className = packageName + "." + file.getName().substring(0, file.getName().length() - 6); // remove .class extension
                    try {
                        Class<?> clazz = Class.forName(className);
                        // Check if the class extends ListenerModule
                        if (classToSearchFor.isAssignableFrom(clazz)) {
                            listenerClasses.add(clazz);
                        }
                    } catch (ClassNotFoundException e) {
                        FrameWorkLogger.error("An error occurred while loading class: " + className, e);
                    }
                }
            }
            return listenerClasses;
        }
        return List.of();
    }
}
