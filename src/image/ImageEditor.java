package image;

import java.awt.*;
import java.io.IOException;

public class ImageEditor extends Image {
    public ImageEditor(String filename) throws IOException {
        super(filename);
    }

    public ImageEditor(Color[][] pixelArray, int width, int height) {
        super(pixelArray, width, height);
    }
}
