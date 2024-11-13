package dev.jsinco.discord.modules.canvas;

import dev.jsinco.discord.framework.util.DNI;
import dev.jsinco.discord.framework.FrameWork;
import dev.jsinco.discord.framework.logging.FrameWorkLogger;
import dev.jsinco.discord.framework.scheduling.ScheduleManager;
import dev.jsinco.discord.framework.scheduling.Tick;
import dev.jsinco.discord.framework.scheduling.Tickable;
import dev.jsinco.discord.framework.scheduling.TimeUnit;
import dev.jsinco.discord.framework.serdes.Serdes;
import dev.jsinco.discord.modules.canvas.encapsulation.DiscordCanvasUser;
import dev.jsinco.discord.modules.canvas.encapsulation.institute.Institution;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

@Tick(unit = TimeUnit.MINUTES, period = 30)
@Getter
@DNI
public class DiscordCanvasUserManager extends Tickable {

    private static final String FOLDER_NAME = "canvas";
    private static final Path dataFolderPath = FrameWork.getDataFolderPath();
    @Getter
    private static final DiscordCanvasUserManager instance = new DiscordCanvasUserManager();

    private final Set<DiscordCanvasUser> loadedDiscordCanvasUsers = new HashSet<>();


    private DiscordCanvasUserManager() {
        loadAllDiscordCanvasUsers();
        ScheduleManager.getInstance().schedule(this);
    }

    @Nullable
    public DiscordCanvasUser loadDiscordCanvasUser(String discordId, boolean cache) {
        Serdes serdes = Serdes.getInstance();
        DiscordCanvasUser user = null;
        try (FileReader fileReader = new FileReader(getDiscordCanvasUserFile(discordId))) {
            user = serdes.deserialize(fileReader, DiscordCanvasUser.class);
            if (cache) {
                loadedDiscordCanvasUsers.add(user);
            }
        } catch (Exception e) {
            FrameWorkLogger.error("Error loading canvas user for " + discordId, e);
        }
        return user;
    }

    public void saveDiscordCanvasUser(DiscordCanvasUser user, boolean unload) {
        Serdes serdes = Serdes.getInstance();
        try (FileWriter fileWriter = new FileWriter(getDiscordCanvasUserFile(user.getDiscordId()))) {
            serdes.serialize(user, fileWriter);
            if (unload) {
                loadedDiscordCanvasUsers.remove(user);
            }
        } catch (IOException e) {
            FrameWorkLogger.error("Error saving canvas user for " + user.getDiscordId(), e);
        }
    }

    public void loadAllDiscordCanvasUsers() {
        File folder = new File(dataFolderPath.toString() + File.separator + FOLDER_NAME);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File[] files = folder.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (file.isFile()) {
                String discordId = file.getName().replace(".json", "");
                loadDiscordCanvasUser(discordId, true);
            }
        }
    }

    public void saveAllDiscordCanvasUsers() {
        for (DiscordCanvasUser user : loadedDiscordCanvasUsers) {
            saveDiscordCanvasUser(user, false);
        }
    }



    public void createLinkedAccount(String discordId, String canvasKey, Institution institution) {
        DiscordCanvasUser user = new DiscordCanvasUser(discordId, canvasKey, institution);
        loadedDiscordCanvasUsers.add(user);
        saveDiscordCanvasUser(user, false);
    }

    public boolean removeLinkedAccount(String discordId) {
        loadedDiscordCanvasUsers.removeIf(user -> user.getDiscordId().equals(discordId));
        File file = new File(dataFolderPath.toString() + File.separator + FOLDER_NAME + File.separator + discordId + ".json");
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }

    @Nullable
    public DiscordCanvasUser getLinkedAccount(String discordId) {
        return loadedDiscordCanvasUsers.stream()
                .filter(user -> user.getDiscordId().equals(discordId))
                .findFirst()
                .orElse(null);
    }




    public File getDiscordCanvasUserFile(String discordId) {
        File file = new File(dataFolderPath.toString() + File.separator + FOLDER_NAME + File.separator + discordId + ".json");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                FrameWorkLogger.error("Error creating file for DiscordCanvasUser: " + discordId, e);
            }
        }
        return file;
    }


    @Override
    public void onTick() {
        saveAllDiscordCanvasUsers();
    }
}
