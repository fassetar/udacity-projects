package com.anthonyfassett.com.apps.udacityandroidapp;

import java.util.List;
import kaaes.spotify.webapi.android.models.Image;

//DOCS:https://github.com/ivamluz/iluz-udacity-android-nanodegree-spotify-streamer
public class ImageHelper {
    public static final int MINIMUM_PREFERRED_IMAGE_WIDTH = 200;
    public static String LOG_TAG = ImageHelper.class.getSimpleName();

    public static Image getPreferredImage(List<Image> images) {
        boolean imagesAvailable = (images != null) && (images.size() > 0);
        if (!imagesAvailable) {
            return null;
        }

        Image image = null;
        for (Image currentImage : images) {
            if (currentImage.width >= MINIMUM_PREFERRED_IMAGE_WIDTH) {
                image = currentImage;
            }
        }

        // Fallback. Only get image at 0th if no image with at least MINIMUM_PREFERRED_IMAGE_WIDTH
        // was found.
        if (image == null) {
            image = images.get(0);
        }

        return image;
    }

    /**
     * Given a list of images, return the URL of the most appropriate one for rendering on the app.
     */
    public static String getPreferredImageUrl(List<Image> images) {
        Image image = getPreferredImage(images);
        if (image == null) {
            return null;
        } else {
            return image.url;
        }
    }
}