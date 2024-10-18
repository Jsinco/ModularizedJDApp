package club.coding.discord.modules.commands;

import club.coding.discord.framework.commands.CommandModule;
import club.coding.discord.framework.commands.DiscordCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class SystemInfoCommand implements CommandModule {

    @DiscordCommand(name = "system", description = "Displays system information")
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.reply("System Info: \n" +
                "  * OS: " + System.getProperty("os.name") + " " + System.getProperty("os.version") + "\n" +
                "  * Architecture: " + System.getProperty("os.arch") + "\n" +
                "  * Available processors (cores): " + Runtime.getRuntime().availableProcessors() + "\n" +
                "  * JVM: " + System.getProperty("java.version") + " (" + System.getProperty("java.vendor") + ")\n" +
                "  * Total memory: " + Runtime.getRuntime().totalMemory() + "\n" +
                "  * Free memory: " + Runtime.getRuntime().freeMemory()).queue();
    }
}
