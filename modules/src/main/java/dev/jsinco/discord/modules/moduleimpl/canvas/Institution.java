package dev.jsinco.discord.modules.moduleimpl.canvas;

import dev.jsinco.discord.framework.logging.FrameWorkLogger;
import dev.jsinco.discord.modules.util.ImageUtil;
import dev.jsinco.discord.modules.util.Util;
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
            Color.GREEN // Too lazy to go color pick USF's color
    ),
    UNKNOWN_INSTITUTION(
            "https://canvas.instructure.com",
            "Unknown Institution",
            "Unknown-Institution",
            Color.GRAY
    )
    ;

    private final String url;
    private final String properName;
    private final String abbreviatedName;
    private final Color color;
    private final KnownRestriction[] knownRestrictions;

    private BufferedImage canvasLogo;

    Institution(String url, String properName, String abbreviatedName, KnownRestriction... knownRestrictions) {
        this.url = url;
        this.properName = properName;
        this.abbreviatedName = abbreviatedName;
        this.color = Color.DARK_GRAY;
        this.knownRestrictions = knownRestrictions;

        this.canvasLogo = loadCanvasLogo();
    }

    Institution(String url, String properName, String abbreviatedName, Color color, KnownRestriction... knownRestrictions) {
        this.url = url;
        this.properName = properName;
        this.abbreviatedName = abbreviatedName;
        this.color = color;
        this.knownRestrictions = knownRestrictions;

        this.canvasLogo = loadCanvasLogo();
    }

    Institution(String url, String properName, String abbreviatedName, String color, KnownRestriction... knownRestrictions) {
        this.url = url;
        this.properName = properName;
        this.abbreviatedName = abbreviatedName;
        this.color = Util.hex(color);
        this.knownRestrictions = knownRestrictions;

        this.canvasLogo = loadCanvasLogo();
    }

    @Nullable
    public BufferedImage loadCanvasLogo() {
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
        return ImageUtil.replaceImageColors(bufferedImage, Util.hex("#e03d29"), color, 150);
    }

    @Nullable
    public FileUpload getCanvasLogoFileUpload()  {
        try {
            return FileUpload.fromData(ImageUtil.bufferedImageToInputStream(loadCanvasLogo(), "png"), "canvas_logo_" + abbreviatedName + ".png");
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
                .setAuthor("Canvas LMS For " + abbreviatedName, url, getCanvasLogoUrl());
    }
}
