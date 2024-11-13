package dev.jsinco.discord.modules.canvas.moduleabstract.interfaces;

/**
 * Interface for both CanvasEvent and CanvasCommand.
 */
public interface CanvasModule extends CanvasEvent, CanvasCommand {

    @Override
    default void register() {
        CanvasEvent.super.register();
        CanvasCommand.super.register();
    }
}
