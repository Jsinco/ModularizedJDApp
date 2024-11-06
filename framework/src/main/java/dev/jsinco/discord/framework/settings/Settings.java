package dev.jsinco.discord.framework.settings;

import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.CustomKey;
import eu.okaeri.configs.annotation.Exclude;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

@Getter @Setter
public final class Settings extends OkaeriYamlConfig {

    @Comment("Send errors to users if a command throws an exception.")
    private boolean sendErrors = false;

    @Comment("Remote Github repository for this project for enhanced error reporting. (Should include '/' at the end)")
    private String repository = "https://github.com/Coding-Club-HCC/DiscordBot/";

    @Comment("Branch of the remote Github repository for this project for enhanced error reporting.")
    private String branch = "master";

    @Comment("The module path.")
    private String module = "modules/src/main/java";

    @Comment("The default online status.")
    private OnlineStatus defaultStatus = OnlineStatus.DO_NOT_DISTURB;

    @Comment("The default activity type.")
    private Activity.ActivityType defaultActivityType = null;

    @Comment("The default activity.")
    private String defaultActivity = "dev.jsinco.discord";

    @Exclude
    @Getter
    private static Settings instance = createConfig(Settings.class, "settings.yml");
}
