package dev.jsinco.discord.framework.settings;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.CustomKey;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class Settings extends OkaeriConfig {

    @CustomKey("enhanced-errors.sendErrors")
    @Comment("Send errors to users if a command throws an exception.")
    public boolean sendErrors = false;

    @CustomKey("enhanced-errors.repository")
    @Comment("Remote Github repository for this project for enhanced error reporting. (Should include '/' at the end)")
    public String repository = "https://github.com/Coding-Club-HCC/DiscordBot/";

    @CustomKey("enhanced-errors.branch")
    @Comment("Branch of the remote Github repository for this project for enhanced error reporting.")
    public String branch = "master";

    @CustomKey("enhanced-errors.module")
    @Comment("The module path.")
    public String module = "modules/src/main/java";
}
