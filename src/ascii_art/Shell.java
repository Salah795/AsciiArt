package ascii_art;

import ascii_output.AsciiOutput;
import ascii_output.ConsoleAsciiOutput;
import ascii_output.HtmlAsciiOutput;
import image.Image;
import image.ImageEditor;
import image_char_matching.SubImgCharMatcher;

import java.io.IOException;

/**
 * The Shell class provides an interactive command-line interface for converting images to ASCII art.
 * It supports various commands for managing the character set, resolution, output format, and more.
 * @author Salah Mahmied
 */
public class Shell {

    // Constants for command processing and messages
    private static final String RUN_PROMPT = ">>> ";
    private static final int COMMAND_TYPE_INDEX = 0;
    private static final int COMMAND_WITH_TYPES_LENGTH = 2;
    private static final String CHARSET_PRINT_COMMAND = "chars";
    private static final String SPLITTER = " ";
    private static final String CHARSET_PRINTING_FORMAT = "%c ";
    private static final String EXIT_COMMAND = "exit";
    private static final String ADD_COMMAND = "add";
    private static final String REMOVE_COMMAND = "remove";
    private static final String RESOLUTION_COMMAND = "res";
    private static final String OUTPUT_COMMAND = "output";
    private static final String ASCII_ART_COMMAND = "asciiArt";
    private static final String HTML_COMMAND = "html";
    private static final String CONSOLE_COMMAND = "console";
    private static final int MIN_LEGAL_CHAR = 32;  // ASCII space character
    private static final int MAX_LEGAL_CHAR = 126; // ASCII tilde character
    private static final int ADD_WITH_COMMAND_LENGTH = 3;
    private static final char RANGE_CHAR = '-';
    private static final int RANGE_FIRST_CHAR_INDEX = 0;
    private static final int RANGE_LAST_CHAR_INDEX = 2;
    private static final int RANGE_INDEX = 1;
    private static final String ADD_SPACE_COMMAND = "space";
    private static final char SPACE = ' ';
    private static final String ADD_ALL_COMMAND = "all";
    private static final int ADD_ONE_CHAR_COMMAND_LENGTH = 1;
    private static final int ADD_ONE_CHAR_COMMAND_INDEX = 0;
    private static final int IMAGE_NAME_INDEX = 0;
    private static final String ADD_COMMAND_EXCEPTION_MESSAGE = "Did not add due to incorrect format.";
    private static final String RESOLUTION_COMMAND_OUTPUT_FORMAT = "Resolution set to %d";
    private static final String REMOVE_COMMAND_EXCEPTION_MESSAGE = "Did not remove due to incorrect format.";
    private static final String RESOLUTION_EXCEEDING_BOUNDARIES_EXCEPTION_MESSAGE =
            "Did not change resolution due to exceeding boundaries.";
    private static final String RESOLUTION_INCORRECT_FORMAT_EXCEPTION_MESSAGE =
            "Did not change resolution due to incorrect format.";
    private static final String INCORRECT_COMMAND_FORMAT_EXCEPTION_MESSAGE =
            "Did not execute due to incorrect command.";
    private static final String ROUND_INCORRECT_FORMAT_EXCEPTION_MESSAGE =
            "Did not change rounding method due to incorrect format.";
    private static final String OUTPUT_INCORRECT_FORMAT_EXCEPTION_MESSAGE =
            "Did not change output method due to incorrect format.";
    private static final String CHARSET_EXCEPTION_MESSAGE =
            "Did not execute. Charset is too small.";
    private static final String RESOLUTION_DOUBLING_COMMAND = "up";
    private static final String RESOLUTION_DOWN_COMMAND = "down";
    private static final char[] DEFAULT_CHARSET = new char[] {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    private static final int COMMAND_SUB_TYPE_INDEX = 1;
    private static final int RESOLUTION_CHANGING_FACTOR = 2;
    private static final int DEFAULT_RESOLUTION = 2;
    private static final String ROUND_ABS = "abs";
    private static final String ROUND_UP = "up";
    private static final String ROUND_DOWN = "down";
    private static final String HTML_FILENAME = "out.html";
    private static final String FONT_NAME = "Courier New";

    // Instance variables
    private final SubImgCharMatcher charMatcher;  // Handles character matching based on brightness
    private final Image paddedImage;             // The input image with padded dimensions
    private int resolution;                      // Current resolution for ASCII art
    private String outputType;                   // Current output type (console or HTML)

    /**
     * Constructs a new Shell instance with the specified image.
     * @param originalImage The image to convert to ASCII art
     */
    public Shell(Image originalImage) {
        this.paddedImage = ImageEditor.padImageDimensions(originalImage);
        this.charMatcher = new SubImgCharMatcher(DEFAULT_CHARSET);
        this.resolution = DEFAULT_RESOLUTION;
        this.outputType = CONSOLE_COMMAND;
    }

    /**
     * Starts the interactive shell session.
     */
    public void run() {
        while (true) {
            System.out.print(RUN_PROMPT);
            String userInput = KeyboardInput.readLine();
            if (userInput.equals(EXIT_COMMAND)) {
                break;
            }
            checkCommand(userInput.split(SPLITTER));
        }
    }

    /**
     * Main entry point for the ASCII art application.
     * @param args Command line arguments (expects image file path as first argument)
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Please provide an image file path as argument");
            return;
        }

        String originalImageFileName = args[IMAGE_NAME_INDEX];
        Image originalImage;
        try {
            originalImage = new Image(originalImageFileName);
        } catch (IOException e) {
            System.err.println("Error loading image: " + e.getMessage());
            return;
        }
        Shell shell = new Shell(originalImage);
        shell.run();
    }

    /**
     * Determines which command to execute based on user input.
     * @param userArguments The command and its arguments split into an array
     */
    private void checkCommand(String[] userArguments) {
        try {
            switch (userArguments[COMMAND_TYPE_INDEX]) {
                case CHARSET_PRINT_COMMAND:
                    charsCommand();
                    break;
                case ADD_COMMAND:
                    addRemoveCommand(userArguments, true);
                    break;
                case REMOVE_COMMAND:
                    addRemoveCommand(userArguments, false);
                    break;
                case RESOLUTION_COMMAND:
                    resolutionCommand(userArguments);
                    System.out.println(String.format(RESOLUTION_COMMAND_OUTPUT_FORMAT, this.resolution));
                    break;
                case OUTPUT_COMMAND:
                    outputCommand(userArguments);
                    break;
                case ASCII_ART_COMMAND:
                    asciiArtCommand();
                    break;
                default:
                    throw new IOException(INCORRECT_COMMAND_FORMAT_EXCEPTION_MESSAGE);
            }
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
    }

    /**
     * Executes the ASCII art generation and outputs the result.
     * @throws IOException If the character set is too small (less than 2 characters)
     */
    private void asciiArtCommand() throws IOException {
        AsciiOutput asciiOutput;
        if (this.charMatcher.getSortedChars().size() < 2) {
            throw new IOException(CHARSET_EXCEPTION_MESSAGE);
        }

        // Generate ASCII art
        AsciiArtAlgorithm asciiArtAlgorithm = new AsciiArtAlgorithm(
                this.charMatcher.getSortedChars(),
                this.paddedImage,
                this.resolution
        );
        char[][] asciiMatrix = asciiArtAlgorithm.run();

        // Output based on selected method
        if (this.outputType.equals(CONSOLE_COMMAND)) {
            asciiOutput = new ConsoleAsciiOutput();
            asciiOutput.out(asciiMatrix);
        } else {
            asciiOutput = new HtmlAsciiOutput(HTML_FILENAME, FONT_NAME);
            asciiOutput.out(asciiMatrix);
        }
    }

    /**
     * Changes the output method (console or HTML).
     * @param userArguments The command arguments
     * @throws IOException If the command format is incorrect
     */
    private void outputCommand(String[] userArguments) throws IOException {
        if (userArguments.length >= COMMAND_WITH_TYPES_LENGTH &&
                userArguments[COMMAND_SUB_TYPE_INDEX].equals(HTML_COMMAND)) {
            this.outputType = HTML_COMMAND;
        } else if (userArguments.length >= COMMAND_WITH_TYPES_LENGTH &&
                userArguments[COMMAND_SUB_TYPE_INDEX].equals(CONSOLE_COMMAND)) {
            this.outputType = CONSOLE_COMMAND;
        } else {
            throw new IOException(OUTPUT_INCORRECT_FORMAT_EXCEPTION_MESSAGE);
        }
    }

    /**
     * Changes the resolution for ASCII art generation.
     * @param userArguments The command arguments
     * @throws IOException If resolution exceeded boundaries or format is incorrect
     */
    private void resolutionCommand(String[] userArguments) throws IOException {
        if (userArguments.length >= COMMAND_WITH_TYPES_LENGTH &&
                userArguments[COMMAND_SUB_TYPE_INDEX].equals(RESOLUTION_DOUBLING_COMMAND)) {
            // Check if doubling resolution would exceed image width
            if (this.resolution * RESOLUTION_CHANGING_FACTOR > this.paddedImage.getWidth()) {
                throw new IOException(RESOLUTION_EXCEEDING_BOUNDARIES_EXCEPTION_MESSAGE);
            }
            this.resolution *= RESOLUTION_CHANGING_FACTOR;
        } else if (userArguments.length >= COMMAND_WITH_TYPES_LENGTH &&
                userArguments[COMMAND_SUB_TYPE_INDEX].equals(RESOLUTION_DOWN_COMMAND)) {
            // Calculate minimum resolution based on image aspect ratio
            double minCharsInRow = Math.max(1, this.paddedImage.getWidth() / this.paddedImage.getHeight());
            if ((double) this.resolution / RESOLUTION_CHANGING_FACTOR < minCharsInRow) {
                throw new IOException(RESOLUTION_EXCEEDING_BOUNDARIES_EXCEPTION_MESSAGE);
            }
            this.resolution /= RESOLUTION_CHANGING_FACTOR;
        } else if (userArguments.length >= COMMAND_WITH_TYPES_LENGTH) {
            throw new IOException(RESOLUTION_INCORRECT_FORMAT_EXCEPTION_MESSAGE);
        }
    }

    /**
     * Handles both add and remove commands for the character set.
     * @param userArguments The command arguments
     * @param addCommand True if this is an add command, false if remove command
     * @throws IOException If the command format is incorrect
     */
    private void addRemoveCommand(String[] userArguments, boolean addCommand) throws IOException {
        if (userArguments.length >= COMMAND_WITH_TYPES_LENGTH &&
                userArguments[COMMAND_SUB_TYPE_INDEX].equals(ADD_ALL_COMMAND)) {
            addRemoveAllCommand(addCommand);
        } else if (userArguments.length >= COMMAND_WITH_TYPES_LENGTH &&
                userArguments[COMMAND_SUB_TYPE_INDEX].equals(ADD_SPACE_COMMAND)) {
            addRemoveSpaceCommand(addCommand);
        } else if (userArguments.length >= COMMAND_WITH_TYPES_LENGTH &&
                userArguments[COMMAND_SUB_TYPE_INDEX].length() == ADD_WITH_COMMAND_LENGTH &&
                userArguments[COMMAND_SUB_TYPE_INDEX].charAt(RANGE_INDEX) == RANGE_CHAR) {
            addRemoveWithRangeCommand(userArguments[COMMAND_SUB_TYPE_INDEX], addCommand);
        } else if (userArguments.length >= COMMAND_WITH_TYPES_LENGTH &&
                userArguments[COMMAND_SUB_TYPE_INDEX].length() == ADD_ONE_CHAR_COMMAND_LENGTH) {
            addRemoveOneCharCommand(userArguments[COMMAND_SUB_TYPE_INDEX], addCommand);
        } else {
            String exception_message = addCommand ? ADD_COMMAND_EXCEPTION_MESSAGE :
                    REMOVE_COMMAND_EXCEPTION_MESSAGE;
            throw new IOException(exception_message);
        }
    }

    /**
     * Adds or removes a single character from the character set.
     * @param argument The character to add/remove
     * @param addCommand True to add, false to remove
     * @throws IOException If the character is outside legal ASCII range
     */
    private void addRemoveOneCharCommand(String argument, boolean addCommand) throws IOException {
        char charToAdd = argument.charAt(ADD_ONE_CHAR_COMMAND_INDEX);
        if (charToAdd < MIN_LEGAL_CHAR || charToAdd > MAX_LEGAL_CHAR) {
            String exception_message = addCommand ? ADD_COMMAND_EXCEPTION_MESSAGE :
                    REMOVE_COMMAND_EXCEPTION_MESSAGE;
            throw new IOException(exception_message);
        } else {
            if (addCommand) {
                this.charMatcher.addChar(charToAdd);
            } else {
                this.charMatcher.removeChar(charToAdd);
            }
        }
    }

    /**
     * Adds or removes a range of characters from the character set.
     * @param argument The range specification (e.g., "a-z")
     * @param addCommand True to add, false to remove
     * @throws IOException If any character in the range is outside legal ASCII range
     */
    private void addRemoveWithRangeCommand(String argument, boolean addCommand) throws IOException {
        int firstChar = argument.charAt(RANGE_FIRST_CHAR_INDEX);
        int lastChar = argument.charAt(RANGE_LAST_CHAR_INDEX);

        // Handle reverse ranges (e.g., z-a)
        if (argument.charAt(RANGE_FIRST_CHAR_INDEX) > argument.charAt(RANGE_LAST_CHAR_INDEX)) {
            firstChar = argument.charAt(RANGE_LAST_CHAR_INDEX);
            lastChar = argument.charAt(RANGE_FIRST_CHAR_INDEX);
        }

        if (firstChar < MIN_LEGAL_CHAR || lastChar > MAX_LEGAL_CHAR) {
            String exception_message = addCommand ? ADD_COMMAND_EXCEPTION_MESSAGE :
                    REMOVE_COMMAND_EXCEPTION_MESSAGE;
            throw new IOException(exception_message);
        }

        // Process each character in the range
        for (int charValue = firstChar; charValue <= lastChar; charValue++) {
            if (addCommand) {
                this.charMatcher.addChar((char) charValue);
            } else {
                this.charMatcher.removeChar((char) charValue);
            }
        }
    }

    /**
     * Adds or removes the space character from the character set.
     * @param addCommand True to add, false to remove
     */
    private void addRemoveSpaceCommand(boolean addCommand) {
        if (addCommand) {
            this.charMatcher.addChar(SPACE);
        } else {
            this.charMatcher.removeChar(SPACE);
        }
    }

    /**
     * Adds or removes all legal ASCII characters from the character set.
     * @param addCommand True to add, false to remove
     */
    private void addRemoveAllCommand(boolean addCommand) {
        for (int charValue = MIN_LEGAL_CHAR; charValue <= MAX_LEGAL_CHAR; charValue++) {
            if (addCommand) {
                this.charMatcher.addChar((char) charValue);
            } else {
                this.charMatcher.removeChar((char) charValue);
            }
        }
    }

    /**
     * Prints the current character set to the console.
     */
    private void charsCommand() {
        for (char character : this.charMatcher.getSortedChars()) {
            System.out.print(String.format(CHARSET_PRINTING_FORMAT, character));
        }
        System.out.println();
    }
}
