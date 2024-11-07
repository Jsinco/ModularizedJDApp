package dev.jsinco.discord.modules.util;

import dev.jsinco.discord.framework.FrameWork;
import dev.jsinco.discord.framework.logging.FrameWorkLogger;
import dev.jsinco.discord.framework.reflect.InjectStatic;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class WebhookUtil {

    @InjectStatic(value = FrameWork.class)
    private static JDA jda;

    public static Webhook createWebhook(TextChannel channel) {
        return channel.createWebhook(jda.getSelfUser().getName() + "-" + channel.getId()).complete();
    }

    public static Webhook getWebhook(TextChannel channel) {
        List<Webhook> webhooks = new ArrayList<>(channel.retrieveWebhooks().complete());
        return webhooks.stream().filter(it -> Objects.equals(it.getOwner(), jda.getSelfUser())).findFirst().orElse(createWebhook(channel));
    }
}
