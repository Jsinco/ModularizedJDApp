package dev.jsinco.discord.modules.moduleimpl.canvas.encapsulation;

import dev.jsinco.discord.framework.logging.FrameWorkLogger;
import dev.jsinco.discord.modules.util.ImageUtil;
import dev.jsinco.discord.modules.util.StringUtil;
import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.utils.FileUpload;
import org.jetbrains.annotations.Nullable;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Getter
public enum Institution {

    HILLSBOROUGH_COMMUNITY_COLLEGE(
            "https://hcc.instructure.com",
            "Hillsborough Community College",
            "HCC",
            "#001e60",
            KnownRestriction.CANNOT_VIEW_ACCOUNTS
    ),
    UNIVERSITY_OF_SOUTH_FLORIDA(
            "https://usflearn.instructure.com",
            "University of South Florida",
            "USF",
            "#016948"
    ),
    UNKNOWN_INSTITUTION(
            "https://canvas.instructure.com", // Must be a valid instructure URL (obviously)
            "Unknown Institution", // The institution's proper name
            "Unknown-Institution", // The institution's abbreviated name
            "#e4fbea" // A color to represent the institution
            // Any known restrictions for this institution's Canvas API (student accounts)
    )
    ;

    private final String url;
    private final String properName;
    private final String abbreviatedName;
    private final Color color;
    private final KnownRestriction[] knownRestrictions;

    private final BufferedImage canvasLogo;

    Institution(String url, String properName, String abbreviatedName, KnownRestriction... knownRestrictions) {
        this.url = url;
        this.properName = properName;
        this.abbreviatedName = abbreviatedName;
        this.color = Color.DARK_GRAY;
        this.knownRestrictions = knownRestrictions;

        this.canvasLogo = getCanvasLogo();
    }

    Institution(String url, String properName, String abbreviatedName, Color color, KnownRestriction... knownRestrictions) {
        this.url = url;
        this.properName = properName;
        this.abbreviatedName = abbreviatedName;
        this.color = color;
        this.knownRestrictions = knownRestrictions;

        this.canvasLogo = getCanvasLogo();
    }

    Institution(String url, String properName, String abbreviatedName, String color, KnownRestriction... knownRestrictions) {
        this.url = url;
        this.properName = properName;
        this.abbreviatedName = abbreviatedName;
        this.color = StringUtil.hex(color);
        this.knownRestrictions = knownRestrictions;

        this.canvasLogo = getCanvasLogo();
    }

    @Nullable
    public BufferedImage getCanvasLogo() {
        return getCanvasLogo(color);
    }

    @Nullable
    public FileUpload getCanvasLogoFileUpload()  {
        try {
            return FileUpload.fromData(ImageUtil.bufferedImageToInputStream(getCanvasLogo(), "png"), "canvas_logo_" + abbreviatedName + ".png");
        } catch (IOException e) {
            FrameWorkLogger.error("Error creating FileUpload for Canvas logo: " + e.getMessage(), e);
            return null;
        }
    }

    public String getCanvasLogoUrl() {
        return "attachment://canvas_logo_" + abbreviatedName + ".png";
    }

    public EmbedBuilder getEmbed() {
        return new EmbedBuilder()
                .setColor(color)
                .setThumbnail(getCanvasLogoUrl())
                .setAuthor("Canvas LMS For " + abbreviatedName, url, getCanvasLogoUrl());
    }


    public static FileUpload getCanvasLogoFileUpload(Color color, String name)  {
        try {
            return FileUpload.fromData(ImageUtil.bufferedImageToInputStream(getCanvasLogo(color), "png"), "canvas_logo_" + name + ".png");
        } catch (IOException e) {
            FrameWorkLogger.error("Error creating FileUpload for Canvas logo: " + e.getMessage(), e);
            return null;
        }
    }

    @Nullable
    public static BufferedImage getCanvasLogo(Color color) {
        BufferedImage bufferedImage;
        try {
            bufferedImage = ImageUtil.loadImageFromResources("canvas_logo.png");
        } catch (IOException e) {
            FrameWorkLogger.error("Error loading Canvas logo: " + e.getMessage());
            return null;
        }
        if (bufferedImage == null) {
            return null;
        }
        return ImageUtil.replaceImageColors(bufferedImage, StringUtil.hex("#e03d29"), color, 150);
    }

    public static String getCanvasLogoUrl(String name) {
        return "attachment://canvas_logo_" + name + ".png";
    }
}
