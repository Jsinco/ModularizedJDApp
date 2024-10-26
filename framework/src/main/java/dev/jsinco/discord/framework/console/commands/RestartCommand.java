package dev.jsinco.discord.framework.console.commands;

import com.sun.tools.javac.Main;
import dev.jsinco.discord.framework.FrameWork;
import dev.jsinco.discord.framework.console.ConsoleCommand;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Collectors;

public class RestartCommand implements ConsoleCommand {
    @Override
    public String name() {
        return "restart";
    }

    @Override
    public void execute(String[] args) {
        // Simulate application logic here
        System.out.println("Application running...");
        //FrameWork.getDiscordApp().shutdownNow();


        // Add a shutdown hook to restart the application
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                System.out.println("Restarting application...11");
                // Get current environment variables
                Map<String, String> env = System.getenv();

                // Get current system properties
                Map<String, String> sysProps = System.getProperties().entrySet().stream()
                        .collect(Collectors.toMap(
                                e -> (String) e.getKey(),
                                e -> (String) e.getValue()
                        ));

                System.out.println("Restarting application...2");

                // Get the current JAR file name
                String jarPath = RestartCommand.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
                String jarName = new File(jarPath).getAbsolutePath();
                System.out.println(jarName);

                // Build the process to restart the application
                ProcessBuilder processBuilder = new ProcessBuilder("java -jar " + jarName);
                processBuilder.environment().putAll(env);

                System.out.println(processBuilder.command());

                // Add system properties to the command
                sysProps.forEach((key, value) -> processBuilder.command().add("-D" + key + "=" + value));

                // Start the new process
                System.out.println("Restarting application...");
                processBuilder.start();
                System.out.println("Restarting application...3");
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        }));

        // Simulate a condition that requires restart
        System.exit(0);
    }
}
