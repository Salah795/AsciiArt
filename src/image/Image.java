package image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Represents an image with pixel data stored as a 2D array of Color objects.
 * Provides functionality to load, access, and save image data.
 * @author Salah Mahmied
 */
public class Image {

    // The 2D array storing pixel color data (row-major order: [height][width])
    private final Color[][] pixelArray;
    private final int width;    // Width of the image in pixels
    private final int height;   // Height of the image in pixels

    /**
     * Constructs an Image by loading from a file.
     * @param filename Path to the image file to load
     * @throws IOException If the file cannot be read or is not a valid image
     */
    public Image(String filename) throws IOException {
        BufferedImage im = ImageIO.read(new File(filename));
        width = im.getWidth();
        height = im.getHeight();

        // Initialize pixel array and populate with Color objects
        pixelArray = new Color[height][width];
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                // Note: BufferedImage uses (x,y) while we store as (row,col)
                pixelArray[row][col] = new Color(im.getRGB(col, row));
            }
        }
    }

    /**
     * Constructs an Image from an existing pixel array.
     * @param pixelArray 2D array of Color objects representing the image
     * @param width Width of the image
     * @param height Height of the image
     */
    public Image(Color[][] pixelArray, int width, int height) {
        this.pixelArray = pixelArray;
        this.width = width;
        this.height = height;
    }

    /**
     * Gets the width of the image in pixels.
     * @return The image width
     */
    public int getWidth() {
        return width;
    }

    /**
     * Gets the height of the image in pixels.
     * @return The image height
     */
    public int getHeight() {
        return height;
    }

    /**
     * Gets the Color of a specific pixel.
     * @param x The row index (vertical position)
     * @param y The column index (horizontal position)
     * @return The Color object at the specified position
     * @note The coordinates use matrix convention (row, column) rather than
     *       traditional image (x,y) convention
     */
    public Color getPixel(int x, int y) {
        return pixelArray[x][y];
    }

    /**
     * Saves the image to a JPEG file.
     * @param fileName The base filename (without extension) to save as
     * @throws RuntimeException If there's an error writing the file
     */
    public void saveImage(String fileName) {
        // Create BufferedImage with same dimensions as our pixel array
        BufferedImage bufferedImage = new BufferedImage(
                pixelArray[0].length,  // width (columns)
                pixelArray.length,     // height (rows)
                BufferedImage.TYPE_INT_RGB
        );

        // Transfer pixel data from Color[][] to BufferedImage
        for (int row = 0; row < pixelArray.length; row++) {
            for (int col = 0; col < pixelArray[row].length; col++) {
                bufferedImage.setRGB(col, row, pixelArray[row][col].getRGB());
            }
        }

        // Write to JPEG file
        File outputfile = new File(fileName + ".jpeg");
        try {
            ImageIO.write(bufferedImage, "jpeg", outputfile);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save image: " + e.getMessage(), e);
        }
    }
}
