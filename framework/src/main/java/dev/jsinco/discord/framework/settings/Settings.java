package dev.jsinco.discord.framework.settings;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class Settings extends OkaeriConfig {

    //@CustomKey("send-errors")
    @Comment("Send errors to users if a command throws an exception.")
    public boolean sendErrors = true;

    @Comment("Remote Github repository for this project for enhanced error reporting.")
    public String repository = "https://github.com/Coding-Club-HCC/DiscordBot/";

    @Comment("Branch of the remote Github repository for this project for enhanced error reporting.")
    public String branch = "master";
}
