package dev.jsinco.discord.modules.moduleimpl.canvas.commands;

import dev.jsinco.discord.framework.commands.DiscordCommand;
import dev.jsinco.discord.modules.moduleimpl.canvas.CanvasFactoryManager;
import dev.jsinco.discord.modules.moduleimpl.canvas.encapsulation.DiscordCanvasUser;
import dev.jsinco.discord.modules.moduleimpl.canvas.moduleabstract.interfaces.CanvasCommandModule;
import edu.ksu.canvas.CanvasApiFactory;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.io.IOException;

@DiscordCommand(name = "canvas-test", description = "Test command for canvas")
public class CanvasTestCommand implements CanvasCommandModule {

    @Override
    public void canvasCommand(SlashCommandInteractionEvent event, DiscordCanvasUser user, boolean ephemeral) throws IOException {

        EmbedBuilder embedBuilder = user.getInstitution().getEmbed();

        embedBuilder.setTitle("Canvas Test Command");
        embedBuilder.setDescription("This is a test command for the Canvas API");
        embedBuilder.setImage(user.getInstitution().getCanvasLogoUrl());


        CanvasApiFactory factory = CanvasFactoryManager.getFactory(user.getInstitution());



        event.replyEmbeds(embedBuilder.build()).setEphemeral(ephemeral).addFiles(user.getInstitution().getCanvasLogoFileUpload()).queue();
//
//        PageReader pageReader = factory.getReader(PageReader.class, user.getOauth());
//        System.out.println(pageReader.listPagesInCourse("141571").stream().map(Page::getTitle).toList());
    }
}
