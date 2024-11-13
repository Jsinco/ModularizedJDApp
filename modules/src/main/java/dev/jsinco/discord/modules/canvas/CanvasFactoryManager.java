package dev.jsinco.discord.modules.canvas;

import dev.jsinco.discord.modules.canvas.encapsulation.institute.Institution;
import edu.ksu.canvas.CanvasApiFactory;

import java.util.HashMap;
import java.util.Map;

public final class CanvasFactoryManager {

    private static final Map<Institution, CanvasApiFactory> factories = new HashMap<>();

    public static CanvasApiFactory getFactory(Institution institution) {
        if (!factories.containsKey(institution)) {
            CanvasApiFactory canvasApiFactory = new CanvasApiFactory(institution.getUrl());
            factories.put(institution, canvasApiFactory);
            return canvasApiFactory;
        }
        return factories.get(institution);
    }
}
