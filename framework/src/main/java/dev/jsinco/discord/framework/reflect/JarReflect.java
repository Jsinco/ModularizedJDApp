package dev.jsinco.discord.framework.reflect;

import com.google.common.reflect.ClassPath;
import dev.jsinco.discord.framework.FrameWork;
import dev.jsinco.discord.framework.logging.FrameWorkLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.CompletionException;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
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


//    public static <T> @NotNull List<Class<? extends T>> findClasses(@NotNull final File file, @NotNull final Class<T> clazz) throws CompletionException {
//        if (!file.exists()) {
//            return Collections.emptyList();
//        }
//
//        final List<Class<? extends T>> classes = new ArrayList<>();
//
//        final List<String> matches = matchingNames(file);
//
//        for (final String match : matches) {
//            try {
//                final URL jar = file.toURI().toURL();
//                try (final URLClassLoader loader = new URLClassLoader(new URL[]{jar}, clazz.getClassLoader())) {
//                    Class<? extends T> aclass = loadClass(loader, match, clazz);
//                    if (aclass != null) {
//                        classes.add(aclass);
//                    }
//                }
//            } catch (final VerifyError ignored) {
//            } catch (IOException | ClassNotFoundException e) {
//                throw new CompletionException(e.getCause());
//            }
//        }
//        return classes;
//    }

    public static List<Class<?>> loadAllClassesFromJar(File jarFile) {
        List<Class<?>> classes = new ArrayList<>();
        try (URLClassLoader classLoader = new URLClassLoader(new URL[]{jarFile.toURI().toURL()}, FrameWork.getCaller().getClassLoader())) {

            try (JarInputStream jarInputStream = new JarInputStream(new FileInputStream(jarFile))) {
                JarEntry jarEntry;
                while ((jarEntry = jarInputStream.getNextJarEntry()) != null) {
                    if (jarEntry.getName().endsWith(".class")) {
                        String className = jarEntry.getName().replaceAll("/", ".").replace(".class", "");

                        try {
                            classes.add(Class.forName(className, true, classLoader));
                        } catch (ClassNotFoundException | NoClassDefFoundError e) {
                            FrameWorkLogger.error("Failed to load class " + className, e);
                        }
                    }
                }
            }
        } catch (IOException e) {
            FrameWorkLogger.error("Error loading classes from JAR", e);
        }
        return classes;
    }

    public static @NotNull List<String> matchingNames(final File file) {
        final List<String> matches = new ArrayList<>();
        try {
            final URL jar = file.toURI().toURL();
            try (final JarInputStream stream = new JarInputStream(jar.openStream())) {
                JarEntry entry;
                while ((entry = stream.getNextJarEntry()) != null) {
                    final String name = entry.getName();
                    if (!name.endsWith(".class")) {
                        continue;
                    }

                    matches.add(name.substring(0, name.lastIndexOf('.')).replace('/', '.'));
                }
            }
        } catch (Exception e) {
            return Collections.emptyList();
        }
        return matches;
    }

    public static <T> @Nullable Class<? extends T> loadClass(final @NotNull URLClassLoader loader, final String match, @NotNull final Class<T> clazz) throws ClassNotFoundException {
        try {
            final Class<?> loaded = loader.loadClass(match);
            if (clazz.isAssignableFrom(loaded)) {
                return (loaded.asSubclass(clazz));
            }
        } catch (final NoClassDefFoundError ignored) {
        }
        return null;
    }

}
