package com.github.andreashosbach.cucumber_reporter.model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Screenshot {
    private static Screenshot screenshot;

    private byte[] image;

    private static byte[] defaultImage;

    static {
        try {
            defaultImage = Files.readAllBytes(Paths.get("src/test/resources/no-image.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Screenshot(byte[] image) {
        this.image = image;
    }

    public static void save(byte[] image) {
        screenshot = new Screenshot(image);
    }

    public static byte[] getScreenshotImage() {
        byte[] bytes;
        if (screenshot != null) {
            bytes = screenshot.image;
            screenshot = null;
        } else {
            bytes = defaultImage;
        }
        return bytes;
    }
}
