package dev.jsinco.discord.framework.reflect;

import com.google.common.reflect.ClassPath;
import dev.jsinco.discord.framework.FrameWork;
import dev.jsinco.discord.framework.logging.FrameWorkLogger;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public class JarReflect {

    public static final String BASE_PACKAGE = FrameWork.getCaller().getPackageName();

    public static Set<Class<?>> getAllClassesFor(@Nullable Class<?>... classes) {
        List<String> packages;
        try {
            packages = getAllPackages(BASE_PACKAGE);
        } catch (IOException e) {
            FrameWorkLogger.error("An error occurred while searching for classes!", e);
            return Set.of();
        }

        Set<Class<?>> allClasses = new HashSet<>();

        for (String pack : packages) {
            try {
                allClasses.addAll(findClasses(pack, List.of(classes)));
            } catch (IOException e) {
                FrameWorkLogger.error("Error while Looking for classes", e);
            }
        }
        return allClasses;
    }

    private static Set<Class<?>> findClasses(String packageName, List<Class<?>> classes) throws IOException {
        ClassLoader classLoader = FrameWork.getCaller().getClassLoader();

        Set<Class<?>> foundClasses = ClassPath.from(classLoader)
                .getTopLevelClasses(packageName)
                .stream()
                .map(ClassPath.ClassInfo::load)
                .collect(Collectors.toSet());

        if (classes.isEmpty()) {
            return foundClasses;
        }

        Set<Class<?>> returnableClasses = new HashSet<>();

        for (Class<?> clazz : foundClasses) {
            for (Class<?> aClass : classes) {
                if (aClass.isAssignableFrom(clazz)) {
                    returnableClasses.add(clazz);
                    break;
                }
            }
        }

        return returnableClasses;
    }

    private static List<String> getAllPackages(String basePackage) throws IOException {
        List<String> packages = new ArrayList<>();
        try (JarFile jarFile = new JarFile(FrameWork.getCaller().getProtectionDomain().getCodeSource().getLocation().getPath())) {
            Enumeration<JarEntry> entries = jarFile.entries();

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.getName().endsWith(".class")) {
                    String className = entry.getName().replace('/', '.').replace(".class", "");
                    if (className.startsWith(basePackage)) {
                        String packageName = className.substring(0, className.lastIndexOf('.'));
                        if (!packages.contains(packageName)) {
                            packages.add(packageName);
                        }
                    }
                }
            }

        }
        return packages;
    }


}
