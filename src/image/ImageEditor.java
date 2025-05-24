package image;

import java.awt.*;
import java.util.ArrayList;

public final class ImageEditor {

    private static final Color WHITE_COLOR_VALUES = new Color(255, 255, 255);
    private static final int SIDES_NUMBER = 2;
    private static final int PADDING_FACTOR = 2;

    private ImageEditor() {}

    public static Image padImageDimensions(Image image) {
        int newWidth = updateDimension(image.getWidth());
        int newHeight = updateDimension(image.getHeight());
        Color[][] newPixelsMatrix = new Color[newHeight][newWidth];
        updateRows(newPixelsMatrix, image);
        updateColumns(newPixelsMatrix, image);
        return new Image(newPixelsMatrix, newWidth, newHeight);
    }

    //TODO check for the possibility of IndexOutOfBoundsException.
    public static ArrayList<Image> getSubImages(Image image, int resolution) {
        ArrayList<Image> subImages = new ArrayList<>();
        int subImagesSize = image.getWidth() / resolution;
        for (int rowIndex = 0; rowIndex < image.getHeight(); rowIndex += subImagesSize) {
            for (int columnIndex = 0; columnIndex < image.getWidth(); columnIndex += subImagesSize) {
                subImages.add(createSubImage(rowIndex, columnIndex, subImagesSize, image));
            }
        }
        return subImages;
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

    //TODO check for the possibility of IndexOutOfBoundsException.
    private static void updateRows(Color[][] pixelsMatrix, Image originalImage) {
        int newPixelsNumberForEachSide = 0;
        if(pixelsMatrix[0].length != originalImage.getWidth()) {
            newPixelsNumberForEachSide = (pixelsMatrix[0].length - originalImage.getWidth()) / SIDES_NUMBER;
        }
        for (int rowIndex = 0; rowIndex < pixelsMatrix.length; rowIndex++) {
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
    }

    //TODO check for the possibility of IndexOutOfBoundsException.
    private static void updateColumns(Color[][] pixelsMatrix, Image originalImage) {
        int newPixelsNumberForEachSide = 0;
        if(pixelsMatrix.length != originalImage.getHeight()) {
            newPixelsNumberForEachSide = (pixelsMatrix.length - originalImage.getHeight()) / SIDES_NUMBER;
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
    }

    private static int updateDimension(int dimension) {
        int counter = 0;
        while (Math.pow(counter, PADDING_FACTOR) < dimension) {
            counter++;
        }
        if (Math.pow(counter, PADDING_FACTOR) > dimension) {
            dimension = (int) Math.pow(counter, PADDING_FACTOR);
        }
        return dimension;
    }
}
