package dev.jsinco.discord.modules.util;

import dev.jsinco.discord.framework.FrameWork;
import dev.jsinco.discord.framework.reflect.InjectStatic;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.Objects;

public final class WebhookUtil {

    @InjectStatic(value = FrameWork.class)
    private static JDA jda;

    public static Webhook createWebhook(TextChannel channel) {
        return channel.createWebhook(jda.getSelfUser().getName() + "-" + channel.getId()).complete();
    }

    public static Webhook getWebhook(TextChannel channel) {
        var webhooks = channel.retrieveWebhooks().complete().stream().filter(Objects::nonNull).filter(it -> it.getOwner().equals(channel.getJDA().getSelfUser())).toList();
        if (webhooks.size() > 1) {
            for (var webhook : webhooks.subList(1, webhooks.size())) {
                webhook.delete().queue();
            }
        }
        return webhooks.isEmpty() ? createWebhook(channel) : webhooks.get(0);
    }
}
