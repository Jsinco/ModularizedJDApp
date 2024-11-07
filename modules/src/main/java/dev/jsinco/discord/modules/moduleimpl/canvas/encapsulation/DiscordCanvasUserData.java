package dev.jsinco.discord.modules.moduleimpl.canvas.encapsulation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class DiscordCanvasUserData implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;


    private boolean notifications = true;
    private Set<Long> notifiedDiscussions = new HashSet<>();
    private Set<Long> notifiedAssignments = new HashSet<>();
}
