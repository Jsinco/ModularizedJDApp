package dev.jsinco.discord.modules.files;

import dev.jsinco.discord.framework.settings.OkaeriYamlConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.Exclude;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter @Setter
public final class ModuleData extends OkaeriYamlConfig {

    @Comment("Serialized reminders for the ReminderModule")
    private List<String> serializedReminders = new ArrayList<>();

    @Comment("The role ids for applying default roles when someone joins the server for the AutoRoleModule")
    private List<String> autoRoleIds = new ArrayList<>();

    private List<String> encryptedCanvasUsers = new ArrayList<>();

    @Exclude
    @Getter
    private static ModuleData instance = createConfig(ModuleData.class, "module-data.yml");
}
