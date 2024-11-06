package dev.jsinco.discord.modules.moduleimpl.webhooks;

import dev.jsinco.discord.framework.events.ListenerModule;
import dev.jsinco.discord.framework.logging.FrameWorkLogger;
import dev.jsinco.discord.framework.util.AbstainRegistration;
import dev.jsinco.discord.modules.util.WebhookUtil;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@AbstainRegistration
public class DeletedMessageResendListener implements ListenerModule {

    // Store up to 300 messages per channel at a time
    private static final int MAX_CACHE_SIZE = 300;
    private static final Map<Channel, Set<Message>> messageCache = new HashMap<>();

    @SubscribeEvent
    public void onMessageSend(MessageReceivedEvent event) {
        long startTime = System.currentTimeMillis();
        if (!event.isFromGuild() || event.getAuthor().isBot() || event.getMessage().isWebhookMessage()) {
            return;
        }

        Set<Message> messageSet = messageCache.computeIfAbsent(event.getChannel(), it -> new HashSet<>());
        if (messageSet.size() >= MAX_CACHE_SIZE) {
            messageSet.remove(messageSet.iterator().next());
        }
        messageSet.add(event.getMessage());

        FrameWorkLogger.info("Cached message in " + (System.currentTimeMillis() - startTime) + "ms");
    }

    @SubscribeEvent
    public void onMessageDelete(MessageDeleteEvent event) {
        long startTime = System.currentTimeMillis();
        if (!event.isFromGuild() || !messageCache.containsKey(event.getChannel())) {
            return;
        }

        Message message = messageCache.get(event.getChannel()).stream().filter(it -> it.getIdLong() == event.getMessageIdLong()).findFirst().orElse(null);
        if (message == null) {
            FrameWorkLogger.info("Message not found in cache took: " + (System.currentTimeMillis() - startTime) + "ms to process");
            return;
        }

        Guild guild = event.getGuild();
        Member member = guild.retrieveMemberById(message.getAuthor().getId()).complete();
        if (member == null || !member.hasPermission(Permission.MESSAGE_SEND) || member.isTimedOut()) {
            return;
        }


        Webhook webhook = WebhookUtil.getWebhook(event.getChannel().asTextChannel());
        webhook.sendMessage(message.getContentRaw() + "\n-# Restored message, ID: " + message.getId())
                .setAvatarUrl(message.getAuthor().getAvatarUrl())
                .setUsername(message.getAuthor().getEffectiveName()).queue();
        messageCache.get(message.getChannel()).remove(message);
        FrameWorkLogger.info("Resent message in " + (System.currentTimeMillis() - startTime) + "ms");
    }
}
