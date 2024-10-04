package dev.jsinco.discord.config;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.serdes.standard.StandardSerdes;
import eu.okaeri.configs.yaml.snakeyaml.YamlSnakeYamlConfigurer;

import java.io.File;
import java.net.URL;

public class ConfigManager {

    // no lombok :(
    private final BotToken botTokenFile; public BotToken getBotTokenFile() { return botTokenFile; }

    public ConfigManager() {
        this.botTokenFile = this.createConfig(BotToken.class, "bot-token.yml");
    }

    private <T extends OkaeriConfig> T createConfig(Class<T> configClass, String filename) {
        return eu.okaeri.configs.ConfigManager.create(configClass, (it) -> {
            it.withConfigurer(new YamlSnakeYamlConfigurer(), new StandardSerdes());
            it.withBindFile(new File(this.getJarParentFile(), filename));
            it.withRemoveOrphans(true);
            it.saveDefaults();
            it.load(true);
        });
    }

    private File getJarParentFile() {
        // Get the URL of the JAR file
        URL jarLocation = getClass().getProtectionDomain().getCodeSource().getLocation();
        // Convert URL to File
        File jarFile = new File(jarLocation.getFile());
        return jarFile.getParentFile();
    }
}

