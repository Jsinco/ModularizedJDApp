package dev.jsinco.discord.modules.moduleimpl.canvas.moduleabstract;

import dev.jsinco.discord.framework.events.ListenerModule;

/**
 * Interface for both CanvasCommandModule and ListenerModule
 */
public interface CanvasModule extends ListenerModule, CanvasCommandModule {

    @Override
    default void register() {
        ListenerModule.super.register();
        CanvasCommandModule.super.register();
    }
}
