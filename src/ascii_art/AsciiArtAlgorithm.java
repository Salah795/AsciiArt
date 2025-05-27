package ascii_art;

import image.Image;
import image.ImageEditor;
import image_char_matching.SubImgCharMatcher;

import java.util.List;

public class AsciiArtAlgorithm {

    private static Image previousPadImage;
    private static int previousResolution;
    private static double[][] previousBrightness;
    private final SubImgCharMatcher charMatcher;
    private final Image padImage;
    private final int resolution;

    public AsciiArtAlgorithm(List<Character> charset, Image image, int resolution) {
        this.resolution = resolution;
        char[] charsetArray = new char[charset.size()];
        for (int index = 0; index < charsetArray.length; index++) {
            charsetArray[index] = charset.get(index).charValue();
        }
        this.charMatcher = new SubImgCharMatcher(charsetArray);
        this.padImage = ImageEditor.padImageDimensions(image);
    }

    public char[][] run() {
        Image[][] subImages = ImageEditor.getSubImages(padImage, resolution);
        char[][] charMatrix = new char[subImages.length][subImages[0].length];
        if(checkPrevious(charMatrix, subImages)) {
            return charMatrix;
        }
        previousPadImage = this.padImage;
        previousResolution = this.resolution;
        previousBrightness = new double[subImages.length][subImages[0].length];
        for (int rowIndex = 0; rowIndex < subImages.length; rowIndex++) {
            for (int columnIndex = 0; columnIndex < subImages[rowIndex].length; columnIndex++) {
                previousBrightness[rowIndex][columnIndex] = ImageEditor.calculateImageBrightness(
                        subImages[rowIndex][columnIndex]);
                charMatrix[rowIndex][columnIndex] = this.charMatcher.getCharByImageBrightness(
                        previousBrightness[rowIndex][columnIndex]);
            }
        }
        return charMatrix;
    }

    //TODO check about the static variables use in the first run when there values are null, is there any
    // exceptions raise in this situation.
    private boolean checkPrevious(char[][] charMatrix, Image[][] subImages) {
        if(this.padImage.equals(previousPadImage) && this.resolution == previousResolution) {
            for (int rowIndex = 0; rowIndex < subImages.length; rowIndex++) {
                for (int columnIndex = 0; columnIndex < subImages[rowIndex].length; columnIndex++) {
                    charMatrix[rowIndex][columnIndex] = this.charMatcher.getCharByImageBrightness(
                            previousBrightness[rowIndex][columnIndex]);
                }
            }
            return true;
        }
        return false;
    }
}
