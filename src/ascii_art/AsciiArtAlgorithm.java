package ascii_art;

import image.Image;
import image.ImageEditor;
import image_char_matching.SubImgCharMatcher;

import java.util.List;

/**
 * This class is responsible for converting an image into ASCII art using a given character set and resolution.
 * It utilizes caching to optimize performance when the same image and resolution are used consecutively.
 * @author Salah Mahmied
 */
public class AsciiArtAlgorithm {

    // Static variables for caching previous calculation results
    private static Image previousPadImage;         // Stores the previously processed padded image
    private static int previousResolution;         // Stores the previously used resolution
    private static double[][] previousBrightness;   // Stores brightness values from previous calculation

    private final SubImgCharMatcher charMatcher;    // Matches brightness values to characters
    private final Image padImage;                   // The padded input image
    private final int resolution;                   // The current resolution for ASCII art

    /**
     * Constructs an AsciiArtAlgorithm instance.
     * @param charset The list of characters to use for the ASCII art
     * @param image The input image to convert
     * @param resolution The resolution (number of sub-images per dimension) for the conversion
     */
    public AsciiArtAlgorithm(List<Character> charset, Image image, int resolution) {
        this.resolution = resolution;
        // Convert List<Character> to char[] for the SubImgCharMatcher
        char[] charsetArray = new char[charset.size()];
        for (int index = 0; index < charsetArray.length; index++) {
            charsetArray[index] = charset.get(index).charValue();
        }
        this.charMatcher = new SubImgCharMatcher(charsetArray);
        // Pad the image to make its dimensions divisible by the resolution
        this.padImage = ImageEditor.padImageDimensions(image);
    }

    /**
     * Runs the ASCII art conversion algorithm.
     * @return A 2D char array representing the ASCII art
     */
    public char[][] run() {
        // Split the padded image into sub-images based on the resolution
        Image[][] subImages = ImageEditor.getSubImages(padImage, resolution);
        char[][] charMatrix = new char[subImages.length][subImages[0].length];

        // Check if we can use cached results from previous run
        if (checkPrevious(charMatrix, subImages)) {
            return charMatrix;
        }

        // No cache available, perform full calculation
        previousPadImage = this.padImage;
        previousResolution = this.resolution;
        previousBrightness = new double[subImages.length][subImages[0].length];

        // Calculate brightness for each sub-image and find matching character
        for (int rowIndex = 0; rowIndex < subImages.length; rowIndex++) {
            for (int columnIndex = 0; columnIndex < subImages[rowIndex].length; columnIndex++) {
                // Calculate and store brightness
                previousBrightness[rowIndex][columnIndex] = ImageEditor.calculateImageBrightness(
                        subImages[rowIndex][columnIndex]);
                // Find character that best matches the brightness
                charMatrix[rowIndex][columnIndex] = this.charMatcher.getCharByImageBrightness(
                        previousBrightness[rowIndex][columnIndex]);
            }
        }
        return charMatrix;
    }

    /**
     * Checks if the current image and resolution match the previous run to use cached brightness values.
     * @param charMatrix The character matrix to populate if cache is available
     * @param subImages The array of sub-images (used for dimensions)
     * @return true if cache was used, false otherwise
     */
    private boolean checkPrevious(char[][] charMatrix, Image[][] subImages) {
        // Check if the current parameters match the previous run
        if (this.padImage.equals(previousPadImage) && this.resolution == previousResolution) {
            // Use cached brightness values to populate the character matrix
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
