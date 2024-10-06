package dev.jsinco.discord.scevents;

import dev.jsinco.discord.events.ListenerModule;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

public class MessageListener implements ListenerModule {

    @SubscribeEvent
    public void onUserSendsMessage(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }

        // Sends a message in the channel saying "Pong!" when a user sends "!ping"
        if (event.getMessage().getContentRaw().equalsIgnoreCase("!ping")) {
            event.getChannel().sendMessage("Pong!").queue();
        }
    }
}
