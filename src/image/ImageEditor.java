package image;

import java.awt.*;

public final class ImageEditor {

    private static final int MAX_RGB_VALUE = 255;
    private static final Color WHITE_COLOR_VALUES = new Color(255, 255, 255);
    private static final int SIDES_NUMBER = 2;
    private static final int PADDING_FACTOR = 2;
    private static final double GREY_SCALE_RED_FACTOR = 0.2126;
    private static final double GREY_SCALE_GREEN_FACTOR = 0.7152;
    private static final double GREY_SCALE_BLUE_FACTOR = 0.0722;

    private ImageEditor() {}

    public static Image padImageDimensions(Image image) {
        int newWidth = updateDimension(image.getWidth());
        Color[][] newPixelsMatrix = new Color[image.getHeight()][newWidth];
        image = updateRows(newPixelsMatrix, image);
        int newHeight = updateDimension(image.getHeight());
        newPixelsMatrix = new Color[newHeight][newWidth];
        image = updateColumns(newPixelsMatrix, image);
        return image;
    }

    public static Image[][] getSubImages(Image image, int resolution) {
        int subImagesSize = image.getWidth() / resolution;
        int rowsNumber = image.getHeight() / subImagesSize;
        Image[][] subImages = new Image[rowsNumber][resolution];
        for (int rowIndex = 0; rowIndex < image.getHeight(); rowIndex += subImagesSize) {
            for (int columnIndex = 0; columnIndex < image.getWidth(); columnIndex += subImagesSize) {
                subImages[rowIndex / subImagesSize][columnIndex / subImagesSize] = createSubImage(
                        rowIndex, columnIndex, subImagesSize, image);
            }
        }
        return subImages;
    }

    public static double calculateImageBrightness(Image image) {
        double greyPixelSum = 0;
        for (int rowIndex = 0; rowIndex < image.getHeight(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < image.getWidth(); columnIndex++) {
                Color currentPixel = image.getPixel(rowIndex, columnIndex);
                greyPixelSum += currentPixel.getRed() * GREY_SCALE_RED_FACTOR +
                        currentPixel.getGreen() * GREY_SCALE_GREEN_FACTOR +
                        currentPixel.getBlue() * GREY_SCALE_BLUE_FACTOR;
            }
        }
        return greyPixelSum / (image.getWidth() * image.getHeight() * MAX_RGB_VALUE);
    }

    private static Image createSubImage(int row, int column, int size, Image image) {
        Color[][] pixelsSubMatrix = new Color[size][size];
        for (int rowIndex = row; rowIndex < row + size; rowIndex++) {
            for (int columnIndex = column; columnIndex < column + size; columnIndex++) {
                pixelsSubMatrix[rowIndex - row][columnIndex - column] = image.getPixel(rowIndex, columnIndex);
            }
        }
        return new Image(pixelsSubMatrix, size, size);
    }

    private static Image updateRows(Color[][] pixelsMatrix, Image originalImage) {
        int newPixelsNumberForEachSide = (pixelsMatrix[0].length - originalImage.getWidth()) / SIDES_NUMBER;
        if(pixelsMatrix[0].length == originalImage.getWidth()) {
            return originalImage;
        }
        for (int rowIndex = 0; rowIndex < originalImage.getHeight(); rowIndex++) {
            for (int colIndex = 0; colIndex < pixelsMatrix[rowIndex].length; colIndex++) {
                if(colIndex < newPixelsNumberForEachSide) {
                    pixelsMatrix[rowIndex][colIndex] = WHITE_COLOR_VALUES;
                } else if (colIndex < newPixelsNumberForEachSide + originalImage.getWidth()) {
                    pixelsMatrix[rowIndex][colIndex] = originalImage.getPixel(rowIndex,
                            colIndex - newPixelsNumberForEachSide);
                } else {
                    pixelsMatrix[rowIndex][colIndex] = WHITE_COLOR_VALUES;
                }
            }
        }
        return new Image(pixelsMatrix, pixelsMatrix[0].length, originalImage.getHeight());
    }

    private static Image updateColumns(Color[][] pixelsMatrix, Image originalImage) {
        int newPixelsNumberForEachSide = (pixelsMatrix.length - originalImage.getHeight()) / SIDES_NUMBER;
        if(pixelsMatrix.length == originalImage.getHeight()) {
            return originalImage;
        }
        for (int columnIndex = 0; columnIndex < pixelsMatrix[0].length; columnIndex++) {
            for (int rowIndex = 0; rowIndex < pixelsMatrix.length; rowIndex++) {
                if(rowIndex < newPixelsNumberForEachSide) {
                    pixelsMatrix[rowIndex][columnIndex] = WHITE_COLOR_VALUES;
                } else if (rowIndex < newPixelsNumberForEachSide + originalImage.getHeight()) {
                    pixelsMatrix[rowIndex][columnIndex] = originalImage.getPixel(rowIndex -
                            newPixelsNumberForEachSide, columnIndex);
                } else {
                    pixelsMatrix[rowIndex][columnIndex] = WHITE_COLOR_VALUES;
                }
            }
        }
        return new Image(pixelsMatrix, pixelsMatrix[0].length, pixelsMatrix.length);
    }

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
