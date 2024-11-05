package dev.jsinco.discord.modules.data;

import dev.jsinco.discord.framework.settings.OkaeriYamlConfig;
import eu.okaeri.configs.annotation.Comment;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class ModuleData extends OkaeriYamlConfig<ModuleData> {

    @Comment("Serialized reminders for the ReminderModule")
    private List<String> serializedReminders = List.of();

    @Comment("The role ids for applying default roles when someone joins the server for the AutoRoleModule")
    private List<String> autoRoleIds = new ArrayList<>();


    @Getter
    private static ModuleData instance = new ModuleData();
    private ModuleData() {
        super(ModuleData.class, "moduledata.yml");
    }
}
