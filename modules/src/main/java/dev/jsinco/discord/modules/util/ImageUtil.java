package dev.jsinco.discord.modules.util;

import dev.jsinco.discord.framework.logging.FrameWorkLogger;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public final class ImageUtil {

    public static BufferedImage loadImageFromResources(String imagePath) throws IOException {
        // Get the resource as InputStream
        URL url = ImageUtil.class.getClassLoader().getResource(imagePath);

        if (url == null) {
            FrameWorkLogger.error("Resource not found: " + imagePath);
            return null;
        }

        // Read the image using ImageIO
        return ImageIO.read(url);
    }

    public static InputStream bufferedImageToInputStream(BufferedImage image, String format) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(image, format, byteArrayOutputStream);
        return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
    }


    public static BufferedImage replaceImageColors(BufferedImage image, Color targetColor, Color replacementColor, int margin) {
        // Get the width and height of the image
        int width = image.getWidth();
        int height = image.getHeight();

        int targetRgb = targetColor.getRGB();
        int replacementRgb = replacementColor.getRGB();

        // Iterate through each pixel in the image
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Get the RGB value of the pixel at (x, y)
                int rgb = image.getRGB(x, y);

                // Check if the pixel color is within the margin of the target color
                if (isWithinMargin(rgb, targetRgb, margin)) {
                    // Replace the pixel color with the replacement color
                    image.setRGB(x, y, replacementRgb);
                }
            }
        }

        return image;
    }



    private static boolean isWithinMargin(int rgb, int targetRgb, int margin) {
        int r1 = (rgb >> 16) & 0xFF;
        int g1 = (rgb >> 8) & 0xFF;
        int b1 = rgb & 0xFF;

        int r2 = (targetRgb >> 16) & 0xFF;
        int g2 = (targetRgb >> 8) & 0xFF;
        int b2 = targetRgb & 0xFF;

        return Math.abs(r1 - r2) <= margin && Math.abs(g1 - g2) <= margin && Math.abs(b1 - b2) <= margin;
    }


    public static Color generateColor(Long input) {
        // Use a hashing function to generate a color
        float hue = (input % 360) / 360.0f; // Ensure hue is between 0 and 1
        float saturation = 0.9f; // High saturation for vibrant colors
        float brightness = 0.9f; // High brightness for vibrant colors
        return Color.getHSBColor(hue, saturation, brightness);
    }
}
