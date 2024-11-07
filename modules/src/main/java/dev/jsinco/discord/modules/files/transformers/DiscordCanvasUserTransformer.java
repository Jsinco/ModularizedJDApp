package dev.jsinco.discord.modules.files.transformers;

import dev.jsinco.discord.framework.serdes.Serdes;
import dev.jsinco.discord.modules.moduleimpl.canvas.encapsulation.DiscordCanvasUser;
import eu.okaeri.configs.schema.GenericsPair;
import eu.okaeri.configs.serdes.BidirectionalTransformer;
import eu.okaeri.configs.serdes.SerdesContext;
import lombok.NonNull;


public class DiscordCanvasUserTransformer extends BidirectionalTransformer<String, DiscordCanvasUser> {

    private static final Serdes serdes = Serdes.getInstance();


    @Override
    public GenericsPair<String, DiscordCanvasUser> getPair() {
        return this.genericsPair(String.class, DiscordCanvasUser.class);
    }

    @Override
    public DiscordCanvasUser leftToRight(@NonNull String data, @NonNull SerdesContext serdesContext) {
        return serdes.deserialize(data, DiscordCanvasUser.class);
    }

    @Override
    public String rightToLeft(@NonNull DiscordCanvasUser data, @NonNull SerdesContext serdesContext) {
        return serdes.serialize(data);
    }
}
