package dev.jsinco.discord;

/**
 * Main Modules class. Entry point for our app and calls our framework to
 * connect to Discord and register modules that can be registered through reflection.
 * @since 1.0
 * @see FrameWork
 * @see AbstractModule
 * @author Jonah
 */
public class Main {

    public static void main(String[] args) {
        FrameWork.start(Main.class);
    }

}