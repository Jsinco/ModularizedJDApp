package dev.jsinco.discord.framework.shutdown;

import dev.jsinco.discord.framework.logging.FrameWorkLogger;

import java.util.ArrayList;
import java.util.List;

public class ShutdownManager {
    private static final List<ShutdownSavable> savables = new ArrayList<>();

    public static void registerSavable(ShutdownSavable savable) {
        savables.add(savable);
    }

    public static void shutDownClasses() {
        for (ShutdownSavable savable : savables) {
            FrameWorkLogger.info("Shutting down/Saving " + savable.getClass().getSimpleName() + "...");
            savable.onShutdown();
        }
    }
}
