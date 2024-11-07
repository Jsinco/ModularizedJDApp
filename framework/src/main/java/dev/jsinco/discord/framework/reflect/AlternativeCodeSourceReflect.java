package dev.jsinco.discord.framework.reflect;

import dev.jsinco.discord.framework.FrameWork;
import dev.jsinco.discord.framework.logging.FrameWorkLogger;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;


public final class AlternativeCodeSourceReflect {

    public static final String BASE_PACKAGE = FrameWork.getCaller().getPackageName();

    public static Set<Class<?>> getAllClassesFor(@Nullable Class<?>... classes) {

        try {
            // Get the URL of the package and its directory structure
            URL packageUrl = AlternativeCodeSourceReflect.class.getClassLoader().getResource(BASE_PACKAGE.replace(".", "/"));

            if (packageUrl != null) {
                // Recursively find all listener classes
                if (classes.length > 0) {
                    Set<Class<?>> allClasses = new HashSet<>();
                    for (Class<?> aClass : classes) {
                        allClasses.addAll(findListenerClassesInPackage(BASE_PACKAGE, packageUrl, aClass));
                    }
                    return allClasses;
                }
                return findListenerClassesInPackage(BASE_PACKAGE, packageUrl, null);
            }
        } catch (IOException e) {
            FrameWorkLogger.error("An error occurred while searching for classes!", e);
        }

        return Set.of();
    }

    private static Set<Class<?>> findListenerClassesInPackage(String packageName, URL packageUrl, @Nullable Class<?> classToSearchFor) throws IOException {
        // Convert the URL into a directory path
        File directory = new File(packageUrl.getFile());

        // If the directory exists, process its contents
        if (directory.exists()) {
            // Get all files in this directory (including subdirectories)
            File[] files = directory.listFiles();

            if (files == null) {
                return Set.of();
            }
            Set<Class<?>> listenerClasses = new HashSet<>();
            for (File file : files) {
                if (file.isDirectory()) {
                    // If it's a directory, recurse into it
                    listenerClasses.addAll(findListenerClassesInPackage(packageName + "." + file.getName(), file.toURI().toURL(), classToSearchFor));
                } else if (file.getName().endsWith(".class")) {
                    // If it's a .class file, check if it's a listener class
                    String className = packageName + "." + file.getName().substring(0, file.getName().length() - 6); // remove .class extension
                    try {

                        Class<?> clazz = Class.forName(className, false, AlternativeCodeSourceReflect.class.getClassLoader());
                        // Check if the class extends ListenerModule
                        if (classToSearchFor == null || classToSearchFor.isAssignableFrom(clazz)) {
                            listenerClasses.add(clazz);
                        }
                    } catch (ClassNotFoundException e) {
                        FrameWorkLogger.error("An error occurred while loading class: " + className, e);
                    }
                }
            }
            return listenerClasses;
        }
        return Set.of();
    }

}
