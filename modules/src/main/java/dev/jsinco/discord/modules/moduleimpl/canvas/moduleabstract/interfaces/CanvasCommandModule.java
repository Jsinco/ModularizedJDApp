package dev.jsinco.discord.modules.moduleimpl.canvas.moduleabstract.interfaces;

import dev.jsinco.discord.framework.commands.CommandManager;
import dev.jsinco.discord.framework.commands.CommandModule;
import dev.jsinco.discord.framework.commands.DiscordCommand;
import dev.jsinco.discord.modules.moduleimpl.canvas.DiscordCanvasUser;
import dev.jsinco.discord.modules.moduleimpl.canvas.Institution;
import dev.jsinco.discord.modules.util.Util;
import edu.ksu.canvas.exception.CanvasException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public interface CanvasCommandModule extends CommandModule {

    void canvasCommand(SlashCommandInteractionEvent event, DiscordCanvasUser canvasUser, boolean ephemeral) throws Exception;
    default List<OptionData> addOptions() {
        return Collections.emptyList();
    };

    @Override
    default void execute(SlashCommandInteractionEvent event) throws Exception {
        DiscordCanvasUser user = DiscordCanvasUser.from(event.getUser());
        boolean ephemeral = Util.getOption(event.getOption("ephemeral"), OptionType.BOOLEAN, true);
        if (user == null) {
            EmbedBuilder embedBuilder = Institution.UNKNOWN_INSTITUTION.getEmbed();
            embedBuilder.setTitle("Unlinked Canvas Account");
            embedBuilder.setDescription("You need to link your Canvas account to use this command!");
            event.replyEmbeds(embedBuilder.build()).addFiles(Institution.UNKNOWN_INSTITUTION.getCanvasLogoFileUpload()).setEphemeral(ephemeral).queue();
            return;
        }

        // Handle Canvas commands in a separate thread to prevent blocking the main thread
        Thread thread = new Thread(() -> {
            try {
                canvasCommand(event, user, ephemeral);
            } catch (CanvasException | NullPointerException e) {
                Institution institution = user.getInstitution();
                EmbedBuilder embedBuilder = institution.getEmbed();

                embedBuilder.setTitle("Something went wrong executing this Canvas query...");
                embedBuilder.setDescription("Due to Canvas' API being very limited, there's a pretty high chance this command wasn't able to be executed due to lack of permissions.\n\n");
                embedBuilder.addField("Error", e.getMessage(), true);
                embedBuilder.addField("Error Type", e.getClass().getSimpleName(), true);
                embedBuilder.addField("Known Institution Restrictions",
                        Arrays.stream(institution.getKnownRestrictions())
                                .map(it -> "- **" + it.name() + "**: " + it.getComment() + "\n-# Author: " + it.getCommentAuthor())
                                .collect(Collectors.joining("\n")),
                        true);
                embedBuilder.addField("Institution", "**" + institution.getAbbreviatedName() + "**: " + institution.getProperName(), false);
                if (!event.isAcknowledged()) {
                    event.replyEmbeds(embedBuilder.build()).addFiles(institution.getCanvasLogoFileUpload()).queue();
                } else {
                    event.getHook().sendMessageEmbeds(embedBuilder.build()).addFiles(institution.getCanvasLogoFileUpload()).queue();
                }
            } catch (Exception e) {
                CommandManager.handleCommandException(event, e);
            }
        });

        thread.setName("canvas-cmd-" + event.getUser().getId());
        thread.start();
    }

    @Override
    default List<OptionData> getOptions() {
        List<OptionData> options = new ArrayList<>(addOptions());
        options.add(new OptionData(OptionType.BOOLEAN, "ephemeral", "Should the response be ephemeral? (Non-visible to others)", false));
        return options;
    }

    @Override
    default DiscordCommand getCommandInfo() {
        DiscordCommand annotation = getClass().getAnnotation(DiscordCommand.class);
        if (annotation == null) {
            try {
                annotation = getClass().getMethod("canvasCommand", SlashCommandInteractionEvent.class, DiscordCanvasUser.class, boolean.class).getAnnotation(DiscordCommand.class);
            } catch (NoSuchMethodException ignored) {
            }
        }
        return annotation;
    }
}
