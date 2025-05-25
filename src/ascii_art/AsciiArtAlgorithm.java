package ascii_art;

import image.Image;
import image.ImageEditor;
import image_char_matching.SubImgCharMatcher;

public class AsciiArtAlgorithm {

    private final SubImgCharMatcher charMatcher;
    private final Image[][] subImages;

    public AsciiArtAlgorithm(char[] charset, Image image, int resolution) {
        this.charMatcher = new SubImgCharMatcher(charset);
        Image padImage = ImageEditor.padImageDimensions(image);
        this.subImages =  ImageEditor.getSubImages(padImage, resolution);
    }

    public char[][] run() {
        char[][] charMatrix = new char[this.subImages.length][this.subImages[0].length];
        for (int rowIndex = 0; rowIndex < this.subImages.length; rowIndex++) {
            for (int columnIndex = 0; columnIndex < this.subImages[rowIndex].length; columnIndex++) {
                double subImageBrightness = ImageEditor.calculateImageBrightness(
                        this.subImages[rowIndex][columnIndex]);
                charMatrix[rowIndex][columnIndex] = this.charMatcher.getCharByImageBrightness(
                        subImageBrightness);
            }
        }
        return charMatrix;
    }
}
