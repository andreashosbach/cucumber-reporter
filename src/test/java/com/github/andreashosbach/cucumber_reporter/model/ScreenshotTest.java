package com.github.andreashosbach.cucumber_reporter.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ScreenshotTest {

    @BeforeEach
    void setUp(){
        Screenshot.reset();
    }

    @Test
    void reset() {
        //given
        byte[] image1 = {1,2,3};
        Screenshot.save("Image1", image1);

        //when
        Screenshot.reset();

        //then
        assertEquals("Not available", Screenshot.getPageName(0));
    }

    @Test
    void save_get() {
        //given
        byte[] image1 = {1,2,3};
        byte[] image2 = {4,5,6};
        byte[] image3 = {7,8,9};

        //when
        Screenshot.save("Image1", image1);
        Screenshot.save("Image2", image2);
        Screenshot.blank();
        Screenshot.save("Image3", image3);

        //then
        assertEquals(image2, Screenshot.getScreenshotImage(1));
        assertEquals("Image2", Screenshot.getPageName(1));
        assertEquals("Not available", Screenshot.getPageName(2));
        assertEquals("Not available", Screenshot.getPageName(5));
        assertEquals(Screenshot.getScreenshotImage(2), Screenshot.getScreenshotImage(5));
    }
}
