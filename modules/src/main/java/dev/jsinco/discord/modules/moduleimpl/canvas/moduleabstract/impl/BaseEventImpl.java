package dev.jsinco.discord.modules.moduleimpl.canvas.moduleabstract.impl;

import dev.jsinco.discord.framework.logging.FrameWorkLogger;
import dev.jsinco.discord.framework.scheduling.Tickable;
import dev.jsinco.discord.framework.scheduling.TimeUnit;
import dev.jsinco.discord.modules.moduleimpl.canvas.encapsulation.DiscordCanvasUser;
import dev.jsinco.discord.modules.moduleimpl.canvas.DiscordCanvasUserManager;
import edu.ksu.canvas.interfaces.CanvasReader;

public abstract class BaseEventImpl extends Tickable {

    public BaseEventImpl(TimeUnit timeUnit, long delay, long period) {
        super(timeUnit, delay, period);
    }

    public abstract void tickEvent(DiscordCanvasUser user) throws Exception;

    @Override
    public void onTick() {
        // Due to how long these REST API calls take, ticks for each event need to be async.
        System.out.println("Ticking CanvasLMSEvent: " + this.getClass().getSimpleName());
        Thread thread = new Thread(() -> {
            for (DiscordCanvasUser user : DiscordCanvasUserManager.getInstance().getLoadedDiscordCanvasUsers()) {
                System.out.println("Ticking CanvasLMSEvent for: " + user.getUser().getName() + " with event: " + this.getClass().getSimpleName());
                try {
                    tickEvent(user);
                } catch (Exception e) {
                    FrameWorkLogger.error("Error ticking CanvasLMSEvent for: " + user.getUser().getName(), e);
                }
            }
        });

        thread.setDaemon(false);
        thread.setName("CanvasLMSEvent-" + this.getClass().getSimpleName());
        thread.start();
    }

    /**
     * For convenience, this method just calls the CanvasLMSEventManager.dispatchEvent method.
     * @param reader The reader to dispatch the event for
     * @param arguments The arguments to pass to the event method
     */
    public void dispatchEvent(Class<? extends CanvasReader> reader, Object... arguments) {
        CanvasLMSEventManager.dispatchEvent(reader, arguments);
    }
}
