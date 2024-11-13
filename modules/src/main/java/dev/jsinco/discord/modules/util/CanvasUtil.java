package dev.jsinco.discord.modules.util;

import dev.jsinco.discord.framework.logging.FrameWorkLogger;
import net.dv8tion.jda.api.utils.FileUpload;
import org.jetbrains.annotations.Nullable;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CanvasUtil {


    private static final Pattern URL_PATTERN = Pattern.compile("https://.*\\.instructure\\.com");

    public static boolean isValidCanvasURL(String url) {
        Matcher matcher = URL_PATTERN.matcher(url);
        return matcher.matches();
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
