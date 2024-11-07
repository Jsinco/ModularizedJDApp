package dev.jsinco.discord.modules.moduleimpl.canvas;

import dev.jsinco.discord.modules.moduleimpl.canvas.encapsulation.Institution;
import edu.ksu.canvas.CanvasApiFactory;

import java.util.HashMap;
import java.util.Map;

public final class CanvasFactoryManager {

    private static final Map<Institution, CanvasApiFactory> factories = new HashMap<>();

    public static CanvasApiFactory getFactory(Institution institution) {
        return factories.get(institution);
    }

    static {
        for (Institution institution : Institution.values()) {
            factories.put(institution, new CanvasApiFactory(institution.getUrl()));
        }
    }
}
