package dev.jsinco.discord.modules.canvas.moduleabstract.impl;

import dev.jsinco.discord.framework.logging.FrameWorkLogger;
import dev.jsinco.discord.framework.scheduling.Tickable;
import dev.jsinco.discord.framework.scheduling.TimeUnit;
import dev.jsinco.discord.modules.canvas.CanvasEventManager;
import dev.jsinco.discord.modules.canvas.CanvasFactoryManager;
import dev.jsinco.discord.modules.canvas.DiscordCanvasUserManager;
import dev.jsinco.discord.modules.canvas.encapsulation.DiscordCanvasUser;
import edu.ksu.canvas.CanvasApiFactory;
import edu.ksu.canvas.interfaces.CanvasReader;

public abstract class BaseEventImpl extends Tickable {

    public BaseEventImpl(TimeUnit timeUnit, long delay, long period) {
        super(timeUnit, delay, period);
    }

    public abstract void onEventCall(DiscordCanvasUser user, CanvasApiFactory factory) throws Exception;

    @Override
    public void onTick() {
        // Due to how long these REST API calls take, ticks for each event need to be async.
        FrameWorkLogger.info("Ticking CanvasLMSEvent: " + this.getClass().getSimpleName());
        Thread thread = new Thread(() -> {
            long start = System.currentTimeMillis();
            for (DiscordCanvasUser user : DiscordCanvasUserManager.getInstance().getLoadedDiscordCanvasUsers()) {
                try {
                    onEventCall(user, CanvasFactoryManager.getFactory(user.getInstitution()));
                } catch (Exception e) {
                    FrameWorkLogger.error("Error ticking CanvasLMSEvent for: " + user.getUser().getName(), e);
                }
            }
            FrameWorkLogger.info("Finished ticking CanvasEvent: " + this.getClass().getSimpleName() + " in " + (System.currentTimeMillis() - start) + "ms");
        });

        thread.setDaemon(false);
        thread.setName("canvas-event");
        thread.start();
    }

    /**
     * For convenience, this method just calls the CanvasLMSEventManager.dispatchEvent method.
     * @param reader The reader to dispatch the event for
     * @param arguments The arguments to pass to the event method
     */
    public void dispatchEvent(Class<? extends CanvasReader> reader, Object... arguments) {
        CanvasEventManager.dispatchEvent(reader, arguments);
    }
}
