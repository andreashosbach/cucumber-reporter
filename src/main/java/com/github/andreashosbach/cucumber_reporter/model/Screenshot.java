package com.github.andreashosbach.cucumber_reporter.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Screenshot {
    private static List<Screenshot> screenshots = new ArrayList<>();

    private byte[] image;
    private String pageName;

    private static byte[] defaultImage;

    static {
        try {
            defaultImage = Files.readAllBytes(Paths.get("src/test/resources/no-image.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Screenshot(String pageName, byte[] image) {
        this.pageName = pageName;
        this.image = image;
    }

    public static void save(String pageName, byte[] image) {
        System.out.println("TAKING SCREENSHOT " + pageName);
        screenshots.add(new Screenshot(pageName, image));
    }

    public static byte[] getScreenshotImage(int index) {
        byte[] bytes;
        if (index < screenshots.size()) {
            bytes = screenshots.get(index).image;
        } else {
            bytes = defaultImage;
        }
        return bytes;
    }

    public static String getPageName(int index) {
        if (index < screenshots.size()) {
            return screenshots.get(index).pageName;
        }
        return "Not available";
    }
}
