package dev.jsinco.discord.modules.moduleimpl.canvas.moduleabstract.interfaces;

/**
 * Interface for both CanvasCommandModule and ListenerModule
 */
public interface CanvasModule extends CanvasLMSEvent, CanvasCommandModule {

    @Override
    default void register() {
        CanvasLMSEvent.super.register();
        CanvasCommandModule.super.register();
    }
}
