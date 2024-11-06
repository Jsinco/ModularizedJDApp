package dev.jsinco.discord.modules.moduleimpl.canvas;

import com.google.common.base.Preconditions;
import dev.jsinco.discord.framework.FrameWork;
import dev.jsinco.discord.framework.reflect.InjectStatic;
import edu.ksu.canvas.oauth.NonRefreshableOauthToken;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.Nullable;

@ToString
@Getter @Setter
public class DiscordCanvasUser {

    @InjectStatic(FrameWork.class)
    private static JDA jda;

    private final String discordId;
    private final String canvasToken;
    private final Institution institution;

    private User user = null;
    private NonRefreshableOauthToken oauth = null;

    public DiscordCanvasUser(String discordId, String canvasToken, Institution institution) {
        Preconditions.checkNotNull(discordId, "Discord ID cannot be null");
        Preconditions.checkNotNull(canvasToken, "Canvas token cannot be null");
        Preconditions.checkNotNull(institution, "Institution cannot be null");

        this.discordId = discordId;
        this.canvasToken = canvasToken;
        this.institution = institution;
    }

    public User getUser() {
        if (user == null) {
            user = jda.retrieveUserById(discordId).complete();
        }
        return user;
    }

    public NonRefreshableOauthToken getOauth() {
        if (oauth == null) {
            oauth = new NonRefreshableOauthToken(canvasToken);
        }
        return oauth;
    }

    @Nullable
    public static DiscordCanvasUser from(User user) {
        return DiscordCanvasUserManager.getInstance().getLinkedAccount(user.getId());
    }
}
