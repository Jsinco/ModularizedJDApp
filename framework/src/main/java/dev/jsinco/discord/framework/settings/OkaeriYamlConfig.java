package dev.jsinco.discord.framework.settings;

import dev.jsinco.discord.framework.FrameWork;
import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.configurer.Configurer;
import eu.okaeri.configs.exception.OkaeriException;
import eu.okaeri.configs.serdes.standard.StandardSerdes;
import eu.okaeri.configs.yaml.snakeyaml.YamlSnakeYamlConfigurer;
import lombok.Getter;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public abstract class OkaeriYamlConfig extends OkaeriConfig {

    protected static final Path dataFolderPath = FrameWork.getDataFolderPath();


    protected static <T extends OkaeriYamlConfig> T createConfig(Class<T> configClass) {
        return createConfig(configClass, configClass.getSimpleName().toLowerCase() + ".yml");
    }

    protected static <T extends OkaeriYamlConfig> T createConfig(Class<T> configClass, String filename) {
        return createConfig(configClass, filename, new YamlSnakeYamlConfigurer());
    }

    protected static <T extends OkaeriYamlConfig> T createConfig(Class<T> configClass, String filename, Configurer configurer) {
        return ConfigManager.create(configClass, (it) -> {
            it.withConfigurer(configurer, new StandardSerdes());
            it.withBindFile(dataFolderPath.resolve(filename));
            it.withRemoveOrphans(true);
            it.saveDefaults();
            it.load(true);
        });
    }

    public void saveAsync() throws OkaeriException {
        CompletableFuture.runAsync(this::save);
    }
}
