package dev.jsinco.discord.modules.canvas.commands;

import dev.jsinco.discord.framework.commands.DiscordCommand;
import dev.jsinco.discord.modules.canvas.CanvasFactoryManager;
import dev.jsinco.discord.modules.canvas.encapsulation.DiscordCanvasUser;
import dev.jsinco.discord.modules.canvas.moduleabstract.interfaces.CanvasCommand;
import edu.ksu.canvas.CanvasApiFactory;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@DiscordCommand(name = "canvas-test", description = "Test command for canvas", guildOnly = false)
public class CanvasTestCommand implements CanvasCommand {

    @Override
    public void canvasCommand(SlashCommandInteractionEvent event, DiscordCanvasUser user, boolean ephemeral) {

        EmbedBuilder embedBuilder = user.getInstitution().getEmbed();

        embedBuilder.setTitle("Canvas Test Command");
        embedBuilder.setDescription(user.toString());
        embedBuilder.setImage(user.getInstitution().getCanvasLogoUrl());


        CanvasApiFactory factory = CanvasFactoryManager.getFactory(user.getInstitution());



        event.replyEmbeds(embedBuilder.build()).setEphemeral(ephemeral).addFiles(user.getInstitution().getCanvasLogoFileUpload()).queue();
//
//        PageReader pageReader = factory.getReader(PageReader.class, user.getOauth());
//        System.out.println(pageReader.listPagesInCourse("141571").stream().map(Page::getTitle).toList());
    }
}
