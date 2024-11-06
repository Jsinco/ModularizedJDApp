package dev.jsinco.discord.modules.moduleimpl.canvas;

import lombok.Getter;

/**
 * This is just a class full of restrictions I know a canvas account using an API token can have.
 * I don't understand Canvas' API well enough to know if the issues with this are a wrapper problem,
 * or just that the school has ensured students can't use their API token for such activities.
 * <p>
 * For the time being, I'm going to assume it's the latter and that some tokens may not have access
 * to certain parts of an institution's API.
 *
 * @author Jonah
 */
@Getter
public enum KnownRestriction {


    CANNOT_VIEW_ACCOUNTS(
            "Jonah",
            "Student accounts on this institution cannot see ANYTHING relating to accounts, including their own account."
    ),
    ;


    private final String commentAuthor;
    private final String comment;

    KnownRestriction(String commentAuthor, String comment) {
        this.commentAuthor = commentAuthor;
        this.comment = comment;
    }
}
