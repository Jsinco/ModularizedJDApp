package club.coding.discord.modules;

import club.coding.discord.framework.AbstractModule;
import club.coding.discord.framework.FrameWork;
import club.coding.discord.framework.reflect.InjectStatic;
import dev.jsinco.abstractjavafilelib.schemas.SnakeYamlConfig;
import net.dv8tion.jda.api.JDA;

/**
 * Main Modules class. Entry point for our app and calls our framework to
 * connect to Discord and register modules that cannot be registered through reflection.
 * @since 1.0
 * @see FrameWork
 * @see AbstractModule
 * @author Jonah
 */
public class Main {

    @InjectStatic(from = FrameWork.class)
    private static JDA jda;
    private static final SnakeYamlConfig saves = new SnakeYamlConfig("saves.yml");


    public static void main(String[] args) {
        FrameWork.start(Main.class);
    }

}