package dev.jsinco.discord.framework.serdes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.jsinco.discord.framework.logging.FrameWorkLogger;
import dev.jsinco.discord.framework.reflect.ReflectionUtil;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


@Getter
public class Serdes {

    private static Serdes instance;
    private final Gson gson;

    private Serdes() {
        Map<Class<?>, Object> typeAdapters = getTypeAdapters();
        GsonBuilder builder = new GsonBuilder();
        for (Map.Entry<Class<?>, Object> entry : typeAdapters.entrySet()) {
            builder.registerTypeAdapter(entry.getKey(), entry.getValue());
        }
        gson = builder.setPrettyPrinting().create();
    }

    public <T> String serialize(T object) {
        return gson.toJson(object);
    }

    public <T> T deserialize(String json, Class<T> schema) {
        return gson.fromJson(json, schema);
    }

    public <T> T deserialize(String json, Class<T> schema, T defaultValue) {
        return gson.fromJson(json, schema);
    }

    public static Serdes getInstance() {
        if (instance == null) {
            instance = new Serdes();
        }
        return instance;
    }

    private static Map<Class<?>, Object> getTypeAdapters() {
        Map<Class<?>, Object> typeAdapters = new HashMap<>();
        Set<Class<?>> classes = ReflectionUtil.getAllClassesFor();

        for (Class<?> clazz : classes) {
            if (!clazz.isAnnotationPresent(TypeAdapter.class)) {
                continue;
            }

            Class<?> rawClass = clazz.getAnnotation(TypeAdapter.class).value();
            try {
                typeAdapters.put(clazz, rawClass.getDeclaredConstructor().newInstance());
            } catch (Exception e) {
                FrameWorkLogger.error("Failed to instantiate TypeAdapter for class " + rawClass.getName(), e);
            }
        }

        return typeAdapters;
    }
}
