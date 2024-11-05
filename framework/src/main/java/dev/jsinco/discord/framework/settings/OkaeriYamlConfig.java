package dev.jsinco.discord.framework.settings;

import dev.jsinco.discord.framework.FrameWork;
import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.serdes.standard.StandardSerdes;
import eu.okaeri.configs.yaml.snakeyaml.YamlSnakeYamlConfigurer;

import java.nio.file.Path;

public abstract class OkaeriYamlConfig<A extends OkaeriYamlConfig> extends OkaeriConfig {

    protected static final Path dataFolderPath = FrameWork.getDataFolderPath();
    protected final Class<A> configClass;
    protected final String filename;

    public OkaeriYamlConfig(Class<A> configClass, String filename) {
        this.configClass = configClass;
        this.filename = filename;
    }

    protected <T extends OkaeriConfig> T createConfig(Class<T> configClass, String filename) {
        return ConfigManager.create(configClass, (it) -> {
            it.withConfigurer(new YamlSnakeYamlConfigurer(), new StandardSerdes());
            it.withBindFile(dataFolderPath.resolve(filename));
            it.withRemoveOrphans(true);
            it.saveDefaults();
            it.load(true);
        });
    }
}
