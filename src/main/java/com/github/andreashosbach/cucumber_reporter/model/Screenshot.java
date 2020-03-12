package com.github.andreashosbach.cucumber_reporter.model;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Screenshot {
    private static final Logger logger = Logger.getGlobal();

    private static List<Screenshot> screenshots = new ArrayList<>();

    private byte[] image;
    private String pageName;

    private static final byte[] defaultImage;

    static {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            BufferedImage image = ImageIO.read(Screenshot.class.getResource("/no-image.png"));
            ImageIO.write(image, "png", baos);
            baos.flush();
            defaultImage = baos.toByteArray();
        } catch (Exception e) {
            logger.severe("Could not load default image for screenshots");
            throw new RuntimeException(e);
        }
    }

    private Screenshot(String pageName, byte[] image) {
        this.pageName = pageName;
        this.image = image;
    }

    public static void reset() {
        screenshots = new ArrayList<>();
        logger.info("Screenshot list reset");
    }

    public static void save(String pageName, byte[] image) {
        screenshots.add(new Screenshot(pageName, image));
        logger.info(String.format("Screenshot of '%s' stored at index %d", pageName, screenshots.size() - 1));
    }

    public static void blank() {
        screenshots.add(new Screenshot("Not available", defaultImage));
    }

    public static byte[] getScreenshotImage(int index) {
        byte[] bytes;
        if (index < screenshots.size()) {
            logger.fine(String.format("Retrieved screenshot %d", index));
            bytes = screenshots.get(index).image;
        } else {
            logger.severe(String.format("Screenshot %d unavailable returning default", index));
            bytes = defaultImage;
        }
        return bytes;
    }

    public static String getPageName(int index) {
        if (index < screenshots.size()) {
            logger.info(String.format("Retrieved page name '%d'", index));
            return screenshots.get(index).pageName;
        }
        logger.severe(String.format("Page name '%d' unavailable", index));
        return "Not available";
    }
}
