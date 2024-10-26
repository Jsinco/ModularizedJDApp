package dev.jsinco.discord.framework.reflect;

import dev.jsinco.discord.framework.FrameWork;

import java.net.URL;
import java.security.CodeSource;
import java.util.Set;

/**
 * Utility class for reflection operations.
 * @since 1.0
 * @author Jonah
 */
public class ReflectionUtil {

    public static Set<Class<?>> getAllClassesFor(Class<?>... classes) {
        CodeSource codeSource = FrameWork.getCaller().getProtectionDomain().getCodeSource();
        if (codeSource != null) {
            URL location = codeSource.getLocation();
            if (location.toString().endsWith(".jar")) {
                return JarReflect.getAllClassesFor(classes);
            }
        }
        return AlternativeCodeSourceReflect.getAllClassesFor(classes);
    }
}
