package dev.jsinco.discord.modules.canvas.encapsulation;

import com.google.common.base.Preconditions;
import dev.jsinco.discord.framework.FrameWork;
import dev.jsinco.discord.framework.reflect.InjectStatic;
import dev.jsinco.discord.framework.serdes.TypeAdapter;
import dev.jsinco.discord.modules.canvas.DiscordCanvasUserManager;
import dev.jsinco.discord.modules.canvas.encapsulation.institute.Institution;
import edu.ksu.canvas.oauth.NonRefreshableOauthToken;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.Nullable;

import java.io.Serial;
import java.io.Serializable;

@TypeAdapter(DiscordCanvasUserTypeAdapter.class)
@Getter @Setter
public class DiscordCanvasUser implements Serializable {


    @Serial
    private static final long serialVersionUID = 1L;
    @InjectStatic(FrameWork.class)
    private static JDA jda;

    private final String discordId;
    private final String canvasToken;
    private final Institution institution;
    private final DiscordCanvasUserData userData;

    private transient User user = null;
    private transient NonRefreshableOauthToken oauth = null;

    public DiscordCanvasUser(String discordId, String canvasToken, Institution institution) {
        Preconditions.checkNotNull(discordId, "Discord ID cannot be null");
        Preconditions.checkNotNull(canvasToken, "Canvas token cannot be null");
        Preconditions.checkNotNull(institution, "Institution cannot be null");

        this.discordId = discordId;
        this.canvasToken = canvasToken;
        this.institution = institution;
        this.userData = new DiscordCanvasUserData();
    }

    public DiscordCanvasUser(String discordId, String canvasToken, Institution institution, DiscordCanvasUserData userData) {
        Preconditions.checkNotNull(discordId, "Discord ID cannot be null");
        Preconditions.checkNotNull(canvasToken, "Canvas token cannot be null");
        Preconditions.checkNotNull(institution, "Institution cannot be null");
        Preconditions.checkNotNull(userData, "User data cannot be null");

        this.discordId = discordId;
        this.canvasToken = canvasToken;
        this.institution = institution;
        this.userData = userData;
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

    @Override
    public String toString() {
        return "DiscordCanvasUser{" +
                "discordId='" + discordId + '\'' +
                ", canvasToken='" + "<HIDDEN>" + '\'' +
                ", institution=" + institution +
                ", userData=" + userData +
                ", user=" + user +
                ", oauth=" + oauth +
                '}';
    }
}
