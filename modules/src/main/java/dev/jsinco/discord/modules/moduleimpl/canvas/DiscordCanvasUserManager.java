package dev.jsinco.discord.modules.moduleimpl.canvas;

import dev.jsinco.discord.framework.logging.FrameWorkLogger;
import dev.jsinco.discord.framework.reflect.InjectStatic;
import dev.jsinco.discord.framework.serdes.Serdes;
import dev.jsinco.discord.framework.shutdown.ShutdownSavable;
import dev.jsinco.discord.modules.cryptography.AESEncrypt;
import dev.jsinco.discord.modules.files.ModuleData;
import dev.jsinco.discord.modules.util.Util;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Getter
public class DiscordCanvasUserManager implements ShutdownSavable {

    @InjectStatic(ModuleData.class)
    private static ModuleData moduleData;

    private static DiscordCanvasUserManager instance;
    private final List<DiscordCanvasUser> loadedDiscordCanvasUsers = new ArrayList<>();

    private DiscordCanvasUserManager() {
        String encryptKey = Util.getFromEnvironment("encrypt_key");
        if (encryptKey != null) {
            AESEncrypt aesEncrypt = new AESEncrypt(encryptKey);
            Serdes serdes = Serdes.getInstance();

            for (String encryptedUser : ModuleData.getInstance().getEncryptedCanvasUsers()) {
                String decryptedUser;
                try {
                    decryptedUser = aesEncrypt.decrypt(encryptedUser);
                } catch (Exception e) {
                    FrameWorkLogger.error("Failed to decrypt canvas user: " + encryptedUser, e);
                    continue;
                }
                DiscordCanvasUser discordCanvasUser = serdes.deserialize(decryptedUser, DiscordCanvasUser.class);
                loadedDiscordCanvasUsers.add(discordCanvasUser);
            }
            FrameWorkLogger.info("Loaded " + loadedDiscordCanvasUsers.size() + " canvas users.");
        } else {
            FrameWorkLogger.error("Failed to load canvas users, missing encryption key.");
        }
    }

    @Override
    public void onShutdown() {
        serializeAndSave();
    }


    public void createLinkedAccount(String discordId, String canvasKey, Institution institution) {
        loadedDiscordCanvasUsers.add(new DiscordCanvasUser(discordId, canvasKey, institution));
        serializeAndSave();
    }

    public void removeLinkedAccount(String discordId) {
        loadedDiscordCanvasUsers.removeIf(user -> user.getDiscordId().equals(discordId));
        serializeAndSave();
    }

    @Nullable
    public DiscordCanvasUser getLinkedAccount(String discordId) {
        return loadedDiscordCanvasUsers.stream()
                .filter(user -> user.getDiscordId().equals(discordId))
                .findFirst()
                .orElse(null);
    }

    public void serializeAndSave() {
        Serdes serdes = Serdes.getInstance();
        AESEncrypt aesEncrypt = new AESEncrypt(Util.getFromEnvironment("encrypt_key"));
        List<String> encryptedUsers = new ArrayList<>();

        for (DiscordCanvasUser discordCanvasUser : loadedDiscordCanvasUsers) {
            try {
                encryptedUsers.add(aesEncrypt.encrypt(serdes.serialize(discordCanvasUser)));
            } catch (Exception e) {
                FrameWorkLogger.error("Failed to encrypt canvas user: " + discordCanvasUser.getDiscordId(), e);
            }
        }
        moduleData.setEncryptedCanvasUsers(encryptedUsers);
        moduleData.save();
    }

    public static DiscordCanvasUserManager getInstance() {
        if (instance == null) {
            instance = new DiscordCanvasUserManager();
        }
        return instance;
    }

}
