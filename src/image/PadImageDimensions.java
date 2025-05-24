package image;

import java.awt.*;

public final class PadImageDimensions {

    private PadImageDimensions() {}

    public static Image padImageDimensions(Image image) {
        int newWidth = updateDimension(image.getWidth());
        int newHeight = updateDimension(image.getHeight());
        Color[][] newPixelsMatrix = new Color[newHeight][newWidth];
        updateRows(newPixelsMatrix, image);
        updateColumns(newPixelsMatrix, image);
        return new Image(newPixelsMatrix, newWidth, newHeight);
    }

    private static void updateRows(Color[][] pixelsMatrix, Image originalImage) {
        if(pixelsMatrix[0].length != originalImage.getWidth()) {
            int newPixelsNumberForEachSide = (pixelsMatrix[0].length - originalImage.getWidth()) / 2;
            for (int rowIndex = 0; rowIndex < pixelsMatrix.length; rowIndex++) {
                for (int colIndex = 0; colIndex < pixelsMatrix[rowIndex].length; colIndex++) {
                    if(colIndex < newPixelsNumberForEachSide) {
                        pixelsMatrix[rowIndex][colIndex] = new Color(255, 255, 255);
                    } else if (colIndex < newPixelsNumberForEachSide + originalImage.getWidth()) {
                        pixelsMatrix[rowIndex][colIndex] = originalImage.getPixel(rowIndex,
                                colIndex - newPixelsNumberForEachSide);
                    } else {
                        pixelsMatrix[rowIndex][colIndex] = new Color(255, 255, 255);
                    }
                }
            }
        }
    }

    private static void updateColumns(Color[][] pixelsMatrix, Image originalImage) {
        if(pixelsMatrix.length != originalImage.getHeight()) {
            int newPixelsNumberForEachSide = (pixelsMatrix.length - originalImage.getHeight()) / 2;
            for (int columnIndex = 0; columnIndex < pixelsMatrix[0].length; columnIndex++) {
                for (int rowIndex = 0; rowIndex < pixelsMatrix.length; rowIndex++) {
                    if(rowIndex < newPixelsNumberForEachSide) {
                        pixelsMatrix[rowIndex][columnIndex] = new Color(255, 255, 255);
                    } else if (rowIndex < newPixelsNumberForEachSide + originalImage.getHeight()) {
                        pixelsMatrix[rowIndex][columnIndex] = originalImage.getPixel(rowIndex -
                                newPixelsNumberForEachSide, columnIndex);
                    } else {
                        pixelsMatrix[rowIndex][columnIndex] = new Color(255, 255, 255);
                    }
                }
            }
        }
    }

    private static int updateDimension(int dimension) {
        int counter = 0;
        while (Math.pow(counter, 2) < dimension) {
            counter++;
        }
        if (Math.pow(counter, 2) > dimension) {
            dimension = (int) Math.pow(counter, 2);
        }
        return dimension;
    }
}
