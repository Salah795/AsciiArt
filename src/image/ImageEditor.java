package image;

import java.awt.*;

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
