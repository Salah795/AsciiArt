package image;

import java.awt.*;

/**
 * Provides utility methods for image manipulation including padding, sub-image extraction,
 * and brightness calculation. All methods are static and the class cannot be instantiated.
 * @author Salah Mahmied
 */
public final class ImageEditor {

    // Constants for image processing
    private static final int MAX_RGB_VALUE = 255;                // Maximum RGB component value
    private static final Color WHITE_COLOR_VALUES = new Color(255, 255, 255);  // White color constant
    private static final int SIDES_NUMBER = 2;                   // Number of sides to pad (left/right or top/bottom)
    private static final int PADDING_FACTOR = 2;                 // Base for dimension padding calculation
    private static final double GREY_SCALE_RED_FACTOR = 0.2126;  // Red coefficient for grayscale conversion
    private static final double GREY_SCALE_GREEN_FACTOR = 0.7152;// Green coefficient for grayscale conversion
    private static final double GREY_SCALE_BLUE_FACTOR = 0.0722; // Blue coefficient for grayscale conversion

    // Private constructor to prevent instantiation
    private ImageEditor() {}

    /**
     * Pads an image's dimensions to the nearest power of 2 with white pixels.
     * @param image The image to pad
     * @return A new Image instance with padded dimensions
     */
    public static Image padImageDimensions(Image image) {
        // First pad the width dimension
        int newWidth = updateDimension(image.getWidth());
        Color[][] newPixelsMatrix = new Color[image.getHeight()][newWidth];
        image = updateRows(newPixelsMatrix, image);

        // Then pad the height dimension
        int newHeight = updateDimension(image.getHeight());
        newPixelsMatrix = new Color[newHeight][newWidth];
        image = updateColumns(newPixelsMatrix, image);

        return image;
    }

    /**
     * Divides an image into a grid of sub-images based on the given resolution.
     * @param image The source image to divide
     * @param resolution The number of sub-images along the width dimension
     * @return A 2D array of sub-images
     */
    public static Image[][] getSubImages(Image image, int resolution) {
        int subImagesSize = image.getWidth() / resolution;
        int rowsNumber = image.getHeight() / subImagesSize;
        Image[][] subImages = new Image[rowsNumber][resolution];

        // Extract each sub-image from the source image
        for (int rowIndex = 0; rowIndex < image.getHeight(); rowIndex += subImagesSize) {
            for (int columnIndex = 0; columnIndex < image.getWidth(); columnIndex += subImagesSize) {
                subImages[rowIndex / subImagesSize][columnIndex / subImagesSize] = createSubImage(
                        rowIndex, columnIndex, subImagesSize, image);
            }
        }
        return subImages;
    }

    /**
     * Calculates the average brightness of an image (normalized to 0-1 range).
     * @param image The image to analyze
     * @return The average brightness value between 0 (dark) and 1 (bright)
     */
    public static double calculateImageBrightness(Image image) {
        double greyPixelSum = 0;

        // Sum weighted brightness values for all pixels
        for (int rowIndex = 0; rowIndex < image.getHeight(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < image.getWidth(); columnIndex++) {
                Color currentPixel = image.getPixel(rowIndex, columnIndex);
                greyPixelSum += currentPixel.getRed() * GREY_SCALE_RED_FACTOR +
                        currentPixel.getGreen() * GREY_SCALE_GREEN_FACTOR +
                        currentPixel.getBlue() * GREY_SCALE_BLUE_FACTOR;
            }
        }

        // Normalize by total pixels and maximum possible value
        return greyPixelSum / (image.getWidth() * image.getHeight() * MAX_RGB_VALUE);
    }

    /**
     * Creates a sub-image from a portion of the source image.
     * @param row Starting row index in source image
     * @param column Starting column index in source image
     * @param size Size of the square sub-image to extract
     * @param image Source image
     * @return New Image instance containing the sub-image
     */
    private static Image createSubImage(int row, int column, int size, Image image) {
        Color[][] pixelsSubMatrix = new Color[size][size];

        // Copy pixels from source image to sub-image
        for (int rowIndex = row; rowIndex < row + size; rowIndex++) {
            for (int columnIndex = column; columnIndex < column + size; columnIndex++) {
                pixelsSubMatrix[rowIndex - row][columnIndex - column] =
                        image.getPixel(rowIndex, columnIndex);
            }
        }
        return new Image(pixelsSubMatrix, size, size);
    }

    /**
     * Updates image rows by adding white padding to reach target width.
     * @param pixelsMatrix The destination pixel matrix
     * @param originalImage The source image
     * @return New Image instance with padded width
     */
    private static Image updateRows(Color[][] pixelsMatrix, Image originalImage) {
        int newPixelsNumberForEachSide = (pixelsMatrix[0].length - originalImage.getWidth()) / SIDES_NUMBER;

        // Return original if no padding needed
        if (pixelsMatrix[0].length == originalImage.getWidth()) {
            return originalImage;
        }

        // Copy original pixels with white padding on both sides
        for (int rowIndex = 0; rowIndex < originalImage.getHeight(); rowIndex++) {
            for (int colIndex = 0; colIndex < pixelsMatrix[rowIndex].length; colIndex++) {
                if (colIndex < newPixelsNumberForEachSide) {
                    // Left padding
                    pixelsMatrix[rowIndex][colIndex] = WHITE_COLOR_VALUES;
                } else if (colIndex < newPixelsNumberForEachSide + originalImage.getWidth()) {
                    // Original image pixels
                    pixelsMatrix[rowIndex][colIndex] = originalImage.getPixel(rowIndex,
                            colIndex - newPixelsNumberForEachSide);
                } else {
                    // Right padding
                    pixelsMatrix[rowIndex][colIndex] = WHITE_COLOR_VALUES;
                }
            }
        }
        return new Image(pixelsMatrix, pixelsMatrix[0].length, originalImage.getHeight());
    }

    /**
     * Updates image columns by adding white padding to reach target height.
     * @param pixelsMatrix The destination pixel matrix
     * @param originalImage The source image
     * @return New Image instance with padded height
     */
    private static Image updateColumns(Color[][] pixelsMatrix, Image originalImage) {
        int newPixelsNumberForEachSide = (pixelsMatrix.length - originalImage.getHeight()) / SIDES_NUMBER;

        // Return original if no padding needed
        if (pixelsMatrix.length == originalImage.getHeight()) {
            return originalImage;
        }

        // Copy original pixels with white padding on top and bottom
        for (int columnIndex = 0; columnIndex < pixelsMatrix[0].length; columnIndex++) {
            for (int rowIndex = 0; rowIndex < pixelsMatrix.length; rowIndex++) {
                if (rowIndex < newPixelsNumberForEachSide) {
                    // Top padding
                    pixelsMatrix[rowIndex][columnIndex] = WHITE_COLOR_VALUES;
                } else if (rowIndex < newPixelsNumberForEachSide + originalImage.getHeight()) {
                    // Original image pixels
                    pixelsMatrix[rowIndex][columnIndex] = originalImage.getPixel(
                            rowIndex - newPixelsNumberForEachSide, columnIndex);
                } else {
                    // Bottom padding
                    pixelsMatrix[rowIndex][columnIndex] = WHITE_COLOR_VALUES;
                }
            }
        }
        return new Image(pixelsMatrix, pixelsMatrix[0].length, pixelsMatrix.length);
    }

    /**
     * Calculates the next power of 2 for dimension padding.
     * @param dimension Original dimension (width or height)
     * @return The smallest power of 2 that is >= the input dimension
     */
    private static int updateDimension(int dimension) {
        int counter = 0;
        while (Math.pow(PADDING_FACTOR, counter) < dimension) {
            counter++;
        }
        if (Math.pow(PADDING_FACTOR, counter) > dimension) {
            dimension = (int) Math.pow(PADDING_FACTOR, counter);
        }
        return dimension;
    }
}
