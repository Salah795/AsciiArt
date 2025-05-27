package ascii_art;

import image.Image;
import image.ImageEditor;
import image_char_matching.SubImgCharMatcher;

import java.io.IOException;

public class Shell {

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
    private static final String ROUND_COMMAND = "round";
    private static final String OUTPUT_COMMAND = "output";
    private static final String ASCII_ART_COMMAND = "asciiArt";
    private static final String HTML_COMMAND = "html";
    private static final String CONSOLE_COMMAND = "console";
    private static final int MIN_LEGAL_CHAR = 32;
    private static final int MAX_LEGAL_CHAR = 126;
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
    private static final String RESOLUTION_EXCEEDING_BOUNDARIES_EXCEPTION_MESSAGE = "Did not change " +
            "resolution due to exceeding boundaries.";
    private static final String RESOLUTION_INCORRECT_FORMAT_EXCEPTION = "Did not change resolution " +
            "due to incorrect format.";
    private static final String INCORRECT_COMMAND_FORMAT_EXCEPTION = "Did not execute " +
            "due to incorrect command.";
    private static final String ROUND_INCORRECT_FORMAT_EXCEPTION = "Did not change rounding method " +
            "due to incorrect format.";
    private static final String OUTPUT_INCORRECT_FORMAT_EXCEPTION = "Did not change output method " +
            "due to incorrect format.";
    private static final String RESOLUTION_DOUBLING_COMMAND = "up";
    private static final String RESOLUTION_DOWN_COMMAND = "down";
    private static final char[] DEFAULT_CHARSET = new char[] {'0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9'};
    private static final int COMMAND_SUB_TYPE_INDEX = 1;
    private static final int RESOLUTION_CHANGING_FACTOR = 2;
    private static final int DEFAULT_RESOLUTION = 2;
    private static final String ROUND_ABS = "abs";
    private static final String ROUND_UP = "up";
    private static final String ROUND_DOWN = "down";

    private final SubImgCharMatcher charMatcher;
    private final Image paddedImage;
    private Image[][] subImages;
    private int resolution;
    private String roundType;
    private String outputType;

    public Shell(Image originalImage) {
        this.paddedImage = ImageEditor.padImageDimensions(originalImage);
        this.charMatcher = new SubImgCharMatcher(DEFAULT_CHARSET);
        this.resolution = DEFAULT_RESOLUTION;
        this.roundType = ROUND_ABS;
        this.outputType = CONSOLE_COMMAND;
        this.subImages = ImageEditor.getSubImages(this.paddedImage, resolution);
    }

    public void run(String imageName) {
        while (true) {
            System.out.print(RUN_PROMPT);
            String userInput = KeyboardInput.readLine();
            if (userInput.equals(EXIT_COMMAND)) {
                break;
            }
            checkCommand(userInput.split(SPLITTER));
        }
    }

    public static void main(String[] args) {
        String originalImageFileName = args[IMAGE_NAME_INDEX];
        Image originalImage;
        try {
            originalImage = new Image(originalImageFileName);
        } catch (IOException _) {
            return;
        }
        Shell shell = new Shell(originalImage);
        shell.run(originalImageFileName);
    }

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
                    //TODO fix the resolution doubling problem.
                    resolutionCommand(userArguments);
                    System.out.println(String.format(RESOLUTION_COMMAND_OUTPUT_FORMAT, this.resolution));
                    break;
                case ROUND_COMMAND:
                    roundCommand(userArguments);
                    break;
                case OUTPUT_COMMAND:
                    outputCommand(userArguments);
                    break;
                case ASCII_ART_COMMAND:
                    //TODO implement the this case.
                    break;
                default:
                    throw new Exception(INCORRECT_COMMAND_FORMAT_EXCEPTION);
            }
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
    }

    private void outputCommand(String[] userArguments) throws Exception {
        if(userArguments.length >= COMMAND_WITH_TYPES_LENGTH &&
        userArguments[COMMAND_SUB_TYPE_INDEX].equals(HTML_COMMAND)) {
            this.outputType = HTML_COMMAND;
        } else if (userArguments.length >= COMMAND_WITH_TYPES_LENGTH &&
        userArguments[COMMAND_SUB_TYPE_INDEX].equals(CONSOLE_COMMAND)) {
            this.outputType = CONSOLE_COMMAND;
        } else {
            throw new Exception(OUTPUT_INCORRECT_FORMAT_EXCEPTION);
        }
    }

    private void roundCommand(String[] userArguments) throws Exception {
        if(userArguments.length >= COMMAND_WITH_TYPES_LENGTH &&
                userArguments[COMMAND_SUB_TYPE_INDEX].equals(ROUND_UP)) {
            this.roundType = ROUND_UP;
        } else if (userArguments.length >= COMMAND_WITH_TYPES_LENGTH &&
                userArguments[COMMAND_SUB_TYPE_INDEX].equals(ROUND_DOWN)) {
            this.roundType = ROUND_DOWN;
        } else if (userArguments.length >= COMMAND_WITH_TYPES_LENGTH &&
                userArguments[COMMAND_SUB_TYPE_INDEX].equals(ROUND_ABS)) {
            this.roundType = ROUND_ABS;
        } else {
            throw new Exception(ROUND_INCORRECT_FORMAT_EXCEPTION);
        }
    }

    private void resolutionCommand(String[] userArguments) throws Exception {
        if(userArguments.length >= COMMAND_WITH_TYPES_LENGTH &&
                userArguments[COMMAND_SUB_TYPE_INDEX].equals(RESOLUTION_DOUBLING_COMMAND)) {
            if(this.resolution * RESOLUTION_CHANGING_FACTOR > this.paddedImage.getWidth()) {
                throw new Exception(RESOLUTION_EXCEEDING_BOUNDARIES_EXCEPTION_MESSAGE);
            }
            this.resolution *= RESOLUTION_CHANGING_FACTOR;
            this.subImages = ImageEditor.getSubImages(this.paddedImage, this.resolution);
        } else if (userArguments.length >= COMMAND_WITH_TYPES_LENGTH &&
                userArguments[COMMAND_SUB_TYPE_INDEX].equals(RESOLUTION_DOWN_COMMAND)) {
            double minCharsInRow = Math.max(1, this.paddedImage.getWidth() / this.paddedImage.getHeight());
            if((double) this.resolution / RESOLUTION_CHANGING_FACTOR < minCharsInRow) {
                throw new Exception(RESOLUTION_EXCEEDING_BOUNDARIES_EXCEPTION_MESSAGE);
            }
            this.resolution /= RESOLUTION_CHANGING_FACTOR;
            this.subImages = ImageEditor.getSubImages(this.paddedImage, this.resolution);
        } else if (userArguments.length >= COMMAND_WITH_TYPES_LENGTH) {
            throw new Exception(RESOLUTION_INCORRECT_FORMAT_EXCEPTION);
        }
    }

    private void addRemoveCommand(String[] userArguments, boolean addCommand) throws
            Exception {
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
                userArguments[COMMAND_SUB_TYPE_INDEX].length()  == ADD_ONE_CHAR_COMMAND_LENGTH) {
            addRemoveOneCharCommand(userArguments[COMMAND_SUB_TYPE_INDEX], addCommand);
        } else {
            String exception_message = addCommand ? ADD_COMMAND_EXCEPTION_MESSAGE :
                    REMOVE_COMMAND_EXCEPTION_MESSAGE;
            throw new Exception(exception_message);
        }
    }

    private void addRemoveOneCharCommand(String argument, boolean addCommand) throws
            Exception {
        char charToAdd = argument.charAt(ADD_ONE_CHAR_COMMAND_INDEX);
        if (charToAdd < MIN_LEGAL_CHAR || charToAdd > MAX_LEGAL_CHAR) {
            String exception_message = addCommand ? ADD_COMMAND_EXCEPTION_MESSAGE :
                    REMOVE_COMMAND_EXCEPTION_MESSAGE;
            throw new Exception(exception_message);
        } else {
            if(addCommand) {
                this.charMatcher.addChar(charToAdd);
            } else {
                this.charMatcher.removeChar(charToAdd);
            }
        }
    }

    private void addRemoveWithRangeCommand(String argument, boolean addCommand) throws
            Exception {
        int firstChar = argument.charAt(RANGE_FIRST_CHAR_INDEX);
        int lastChar = argument.charAt(RANGE_LAST_CHAR_INDEX);
        if(argument.charAt(RANGE_FIRST_CHAR_INDEX) > argument.charAt(RANGE_LAST_CHAR_INDEX)) {
            firstChar = argument.charAt(RANGE_LAST_CHAR_INDEX);
            lastChar = argument.charAt(RANGE_FIRST_CHAR_INDEX);
        }
        if (firstChar < MIN_LEGAL_CHAR || lastChar > MAX_LEGAL_CHAR) {
            String exception_message = addCommand ? ADD_COMMAND_EXCEPTION_MESSAGE :
                    REMOVE_COMMAND_EXCEPTION_MESSAGE;
            throw new Exception(exception_message);
        }
        for (int charValue = firstChar; charValue <= lastChar; charValue++) {
            if(addCommand) {
                this.charMatcher.addChar((char) charValue);
            } else {
                this.charMatcher.removeChar((char) charValue);
            }
        }
    }

    private void addRemoveSpaceCommand(boolean addCommand) {
        if (addCommand) {
            this.charMatcher.addChar(SPACE);
        } else {
            this.charMatcher.removeChar(SPACE);
        }
    }

    private void addRemoveAllCommand(boolean addCommand) {
        for (int charValue = MIN_LEGAL_CHAR; charValue <= MAX_LEGAL_CHAR; charValue++) {
            if(addCommand) {
                this.charMatcher.addChar((char)charValue);
            } else {
                this.charMatcher.removeChar((char)charValue);
            }
        }
    }

    private void charsCommand() {
        for (char character: this.charMatcher.getSortedCharset()) {
            System.out.print(String.format(CHARSET_PRINTING_FORMAT, character));
        }
        System.out.println();
    }
}
