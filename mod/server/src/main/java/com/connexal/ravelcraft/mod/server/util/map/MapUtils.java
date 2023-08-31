package com.connexal.ravelcraft.mod.server.util.map;

import net.minecraft.item.map.MapState;

import java.awt.image.BufferedImage;

public class MapUtils {
    public static final int MAP_WIDTH = 128;
    public static final int MAP_HEIGHT = 128;

    public static void applyImageToMap(MapState state, BufferedImage image, boolean dithering) {
        BufferedImage resizedImage = resizeImage(image);
        int[][] palette = MapColour.generateColourPalette();

        for (int y1 = 0; y1 < 128; y1++) {
            int y = y1 * 128;
            for (int x = 0; x < 128; x++) {
                int colour = resizedImage.getRGB(x, y1);
                int r = (colour >> 16) & 0xFF;
                int g = (colour >> 8) & 0xFF;
                int b = colour & 0xFF;
                int a = (colour >> 24) & 0xFF;

                int index = findMatchingColour(r, g, b, a, palette);
                state.colors[y + x] = (byte) index;

                if (dithering) {
                    int newPixel = (a << 24) | (palette[index][0] << 16) | (palette[index][1] << 8) | palette[index][2];
                    resizedImage.setRGB(x, y1, newPixel);

                    //Calculate the error
                    int[] paletteColour = palette[index];
                    r -= paletteColour[0];
                    g -= paletteColour[1];
                    b -= paletteColour[2];

                    distributeError(resizedImage, palette, x, y, r, g, b, a);
                }
            }
        }
    }

    private static BufferedImage resizeImage(BufferedImage input) {
        BufferedImage output = new BufferedImage(MAP_WIDTH, MAP_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        output.getGraphics().drawImage(input, 0, 0, MAP_WIDTH, MAP_HEIGHT, null);
        return output;
    }

    private static int findMatchingColour(int r, int g, int b, int a, int[][] palette) {
        int nearestIndex = 0;
        int minDistance = Integer.MAX_VALUE;

        for (int i = 0; i < palette.length; i++) {
            int[] colour = palette[i];
            //Extra bias for alpha
            int distance = Math.abs(colour[0] - r) + Math.abs(colour[1] - g) + Math.abs(colour[2] - b) + (Math.abs(colour[3] - a) * 100);

            if (distance < minDistance) {
                minDistance = distance;
                nearestIndex = i;
            }
        }

        return nearestIndex;
    }

    private static void distributeError(BufferedImage image, int[][] palette, int x, int y, int errorR, int errorG, int errorB, int alpha) {
        //Apply Floyd-Steinberg dithering
        //For simplicity, let's distribute the error to the neighboring right and bottom pixels

        distributeErrorToPixel(image, palette, x + 1, y, errorR, errorG, errorB, alpha, 7.0 / 16.0);
        distributeErrorToPixel(image, palette, x - 1, y + 1, errorR, errorG, errorB, alpha, 3.0 / 16.0);
        distributeErrorToPixel(image, palette, x, y + 1, errorR, errorG, errorB, alpha, 5.0 / 16.0);
        distributeErrorToPixel(image, palette, x + 1, y + 1, errorR, errorG, errorB, alpha, 1.0 / 16.0);
    }

    public static void distributeErrorToPixel(BufferedImage image, int[][] palette, int x, int y, int errorR, int errorG, int errorB, int alpha, double factor) {
        if (x >= 0 && x < image.getWidth() && y >= 0 && y < image.getHeight()) {
            int pixel = image.getRGB(x, y);

            int red = ((pixel >> 16) & 0xFF) + (int) (errorR * factor);
            int green = ((pixel >> 8) & 0xFF) + (int) (errorG * factor);
            int blue = (pixel & 0xFF) + (int) (errorB * factor);

            red = Math.min(Math.max(red, 0), 255);
            green = Math.min(Math.max(green, 0), 255);
            blue = Math.min(Math.max(blue, 0), 255);

            int[] paletteColor = { red, green, blue }; // New color after dithering
            int newPaletteIndex = findMatchingColour(red, green, blue, alpha, palette);
            int[] newPaletteColor = palette[newPaletteIndex]; // Nearest color in the palette

            int newPixel = (alpha << 24) | (newPaletteColor[0] << 16) | (newPaletteColor[1] << 8) | newPaletteColor[2];
            image.setRGB(x, y, newPixel);
        }
    }
}
