package dev.jsinco.discord.framework.console;

import dev.jsinco.discord.framework.logging.FrameWorkLogger;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Manages console commands.
 * @since 1.0
 * @see ConsoleCommand
 * @author Jonah
 */
@Getter
public class ConsoleCommandManager {

    private static ConsoleCommandManager singleton;

    public static ConsoleCommandManager getInstance() {
        if (singleton == null) {
            singleton = new ConsoleCommandManager();
            singleton.start();
        }
        return singleton;
    }

    private final Map<String, ConsoleCommand> commands = new HashMap<>();


    private ConsoleCommandManager() {
    }


    public ConsoleCommandManager registerCommand(ConsoleCommand command) {
        commands.put(command.name().toLowerCase(), command);
        return this;
    }

    private void start() {
        Thread thread = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String input;
                try {
                    input = scanner.nextLine();
                } catch (NoSuchElementException e) {
                    FrameWorkLogger.info("Console input closed. Exiting...");
                    break;
                }

                if (input.isEmpty()) {
                    continue;
                }

                String[] args = input.split(" ");
                ConsoleCommand command = commands.get(args[0].toLowerCase());
                if (command != null) {
                    command.execute(args);
                } else {
                    FrameWorkLogger.info("Command not found. Type 'help' for a list of commands.");
                }
            }
        });
        thread.setName("cmd-thread");
        thread.start();
    }
}
