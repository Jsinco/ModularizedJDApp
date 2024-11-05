package dev.jsinco.discord.modules.automations;

import dev.jsinco.discord.framework.commands.DiscordCommand;
import dev.jsinco.discord.framework.events.ListenerModule;
import dev.jsinco.discord.framework.logging.FrameWorkLogger;
import dev.jsinco.discord.framework.reflect.InjectStatic;
import dev.jsinco.discord.framework.util.Module;
import dev.jsinco.discord.modules.data.ModuleData;
import dev.jsinco.discord.modules.util.Util;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;
import java.util.Objects;

public class AutoRoleModule implements Module {

    @InjectStatic(ModuleData.class)
    private static ModuleData moduleData;

    @SubscribeEvent
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        if (moduleData.getAutoRoleIds().isEmpty()) {
            return;
        }

        List<Role> roles = moduleData.getAutoRoleIds().stream()
                .map(event.getGuild()::getRoleById)
                .filter(Objects::nonNull)
                .toList();

        event.getGuild().modifyMemberRoles(event.getMember(), roles).queue();
        FrameWorkLogger.info("Auto role(s) assigned to " + event.getMember().getUser().getAsTag() + " in guild " + event.getGuild().getName() + " (" + event.getGuild().getId() + ").");
    }

    @DiscordCommand(name = "autorole", description = "Automatically assign a role to new members.", permission = Permission.MANAGE_ROLES)
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Role role = Util.getOption(event.getOption("role"), OptionType.ROLE);
        Role removeRole = Util.getOption(event.getOption("remove_role"), OptionType.ROLE);

        StringBuilder response = new StringBuilder();

        if (role != null) {
            moduleData.getAutoRoleIds().add(role.getId());
            response.append("Role **").append(role.getName()).append("** added to auto role list.");
        }
        if (removeRole != null) {
            moduleData.getAutoRoleIds().remove(removeRole.getId());
            response.append("Role **").append(removeRole.getName()).append("** removed from auto role list.");
        }

        event.reply(response.toString()).setEphemeral(true).queue();
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.ROLE, "role", "The role to automatically assign to new members.", false),
                new OptionData(OptionType.ROLE, "remove_role", "Remove a role from being automatically assigned to new guild members.", false)
        );
    }
}
