package dev.jsinco.discord.config;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.CustomKey;

public final class BotToken extends OkaeriConfig {

    @CustomKey("discord-bot-token")
    @Comment("Discord Bot Token. Needed for this application to run!")
    public String TOKEN = "BOT_TOKEN_HERE";
}
