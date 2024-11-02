package dev.jsinco.discord.framework.settings;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.serdes.standard.StandardSerdes;
import eu.okaeri.configs.yaml.snakeyaml.YamlSnakeYamlConfigurer;
import lombok.Getter;

import java.nio.file.Path;

@Getter
public class FrameWorkFileManager {

    private final Path dataFolderPath;

    private final Settings settings;

    public FrameWorkFileManager(Path dataFolderPath) {
        this.dataFolderPath = dataFolderPath;


        this.settings = this.createConfig(Settings.class, "settings.yml");
    }


    private <T extends OkaeriConfig> T createConfig(Class<T> configClass, String filename) {
        return eu.okaeri.configs.ConfigManager.create(configClass, (it) -> {
            it.withConfigurer(new YamlSnakeYamlConfigurer(), new StandardSerdes());
            it.withBindFile(this.dataFolderPath.resolve(filename));
            it.withRemoveOrphans(true);
            it.saveDefaults();
            it.load(true);
        });
    }
}
