package club.coding.discord.framework.console;

/**
 * Interface for console commands.
 * @since 1.0
 * @author Jonah
 * @see ConsoleCommandManager
 */
public interface ConsoleCommand {
    /**
     * @return The name of the command.
     */
    String name();

    /**
     * Executes the command.
     * @param args The arguments passed to the command.
     */
    void execute(String[] args);
}
