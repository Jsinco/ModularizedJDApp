package dev.jsinco.discord.modules.files;

import dev.jsinco.discord.framework.settings.OkaeriYamlConfig;
import dev.jsinco.discord.modules.files.transformers.DiscordCanvasUserTransformer;
import dev.jsinco.discord.modules.moduleimpl.canvas.encapsulation.DiscordCanvasUser;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.Exclude;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public final class ModuleData extends OkaeriYamlConfig {

    @Comment("Serialized reminders for the ReminderModule")
    private List<String> serializedReminders = new ArrayList<>();

    @Comment("The role ids for applying default roles when someone joins the server for the AutoRoleModule")
    private List<String> autoRoleIds = new ArrayList<>();

    @Comment("TODO: I would make a new file for each user instead of this.")
    private List<DiscordCanvasUser> discordCanvasUsers = new ArrayList<>();

    @Exclude
    @Getter
    private static ModuleData instance = createConfig(ModuleData.class, "module-data.yml", new DiscordCanvasUserTransformer());
}
