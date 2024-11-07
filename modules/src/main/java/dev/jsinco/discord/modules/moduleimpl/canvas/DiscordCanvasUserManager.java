package dev.jsinco.discord.modules.moduleimpl.canvas;

import dev.jsinco.discord.framework.FrameWork;
import dev.jsinco.discord.framework.logging.FrameWorkLogger;
import dev.jsinco.discord.framework.reflect.InjectStatic;
import dev.jsinco.discord.framework.scheduling.Tick;
import dev.jsinco.discord.framework.scheduling.Tickable;
import dev.jsinco.discord.framework.scheduling.TimeUnit;
import dev.jsinco.discord.framework.shutdown.ShutdownManager;
import dev.jsinco.discord.framework.shutdown.ShutdownSavable;
import dev.jsinco.discord.modules.files.ModuleData;
import dev.jsinco.discord.modules.moduleimpl.canvas.encapsulation.DiscordCanvasUser;
import dev.jsinco.discord.modules.moduleimpl.canvas.encapsulation.Institution;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Tick(unit = TimeUnit.MINUTES, period = 30)
@Getter
public class DiscordCanvasUserManager extends Tickable implements ShutdownSavable {

    @InjectStatic(ModuleData.class)
    private static ModuleData moduleData;

    private static DiscordCanvasUserManager instance;

    private final List<DiscordCanvasUser> loadedDiscordCanvasUsers;


    private DiscordCanvasUserManager() {
        this.loadedDiscordCanvasUsers = moduleData.getDiscordCanvasUsers();
        FrameWorkLogger.info("Loaded " + loadedDiscordCanvasUsers.size() + " canvas users.");


        // saving
        FrameWork.registerTickable(this);
        ShutdownManager.registerSavable(this);
    }

    @Override
    public void onTick() {
        moduleData.save();
    }


    public void createLinkedAccount(String discordId, String canvasKey, Institution institution) {
        loadedDiscordCanvasUsers.add(new DiscordCanvasUser(discordId, canvasKey, institution));
        moduleData.save();
    }

    public void removeLinkedAccount(String discordId) {
        loadedDiscordCanvasUsers.removeIf(user -> user.getDiscordId().equals(discordId));
        moduleData.save();
    }

    @Nullable
    public DiscordCanvasUser getLinkedAccount(String discordId) {
        return loadedDiscordCanvasUsers.stream()
                .filter(user -> user.getDiscordId().equals(discordId))
                .findFirst()
                .orElse(null);
    }

    public static DiscordCanvasUserManager getInstance() {
        if (instance == null) {
            instance = new DiscordCanvasUserManager();
        }
        return instance;
    }

    @Override
    public void onShutdown() {

        moduleData.save();
    }
}
