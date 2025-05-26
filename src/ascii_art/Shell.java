package ascii_art;

import image.Image;
import image_char_matching.SubImgCharMatcher;

import java.io.IOException;
import java.util.PrimitiveIterator;

public class Shell {

    private static final String RUN_PROMPT = ">>> ";
    private static final String CHARSET_PRINT_COMMAND = "chars";
    private static final String SPLITTER = " ";
    private static final String CHARSET_PRINTING_FORMAT = "%c ";
    private static final String EXIT_COMMAND = "exit";
    private static final String ADD_COMMAND = "add";
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
    private static final char[] DEFAULT_CHARSET = new char[] {'0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9'};

    private SubImgCharMatcher charMatcher;

    public Shell(String originalImageFileName) {
        try {
            this.charMatcher = new SubImgCharMatcher(DEFAULT_CHARSET);
            Image originalImage = new Image(originalImageFileName);
        } catch (IOException _) {}
    }

    public void run(String imageName) {
        while (true) {
            System.out.println(RUN_PROMPT);
            String userInput = KeyboardInput.readLine();
            if (userInput.equals(EXIT_COMMAND)) {
                break;
            }
            checkCommand(userInput.split(SPLITTER));
        }
    }

    public static void main(String[] args) {
        String originalImageFileName = args[IMAGE_NAME_INDEX];
        Shell shell = new Shell(originalImageFileName);
        shell.run(originalImageFileName);
    }

    private void checkCommand(String[] userArguments) {
        switch (userArguments[0]) {
            case CHARSET_PRINT_COMMAND:
                charsCommand();
                break;
            case ADD_COMMAND:
                try {
                    addCommand(userArguments[1]);
                } catch (IllegalAddCommandException exception) {
                    System.out.println(exception.getMessage());
                }
                break;
        }
    }

    private void addCommand(String argument) throws IllegalAddCommandException {
        if (argument.equals(ADD_ALL_COMMAND)) {
            addAllCommand();
        } else if (argument.equals(ADD_SPACE_COMMAND)) {
            addSpaceCommand();
        } else if (argument.length() == ADD_WITH_COMMAND_LENGTH &&
                argument.charAt(RANGE_INDEX) == RANGE_CHAR) {
            addWithRangeCommand(argument);
        } else if (argument.length()  == ADD_ONE_CHAR_COMMAND_LENGTH) {
            addOneCharCommand(argument);
        } else {
            throw new IllegalAddCommandException();
        }
    }

    private void addOneCharCommand(String argument) throws IllegalAddCommandException {
        char charToAdd = argument.charAt(ADD_ONE_CHAR_COMMAND_INDEX);
        if (charToAdd < MIN_LEGAL_CHAR || charToAdd > MAX_LEGAL_CHAR) {
            throw new IllegalAddCommandException();
        } else {
            this.charMatcher.addChar(charToAdd);
        }
    }

    private void addWithRangeCommand(String argument) throws IllegalAddCommandException {
        int firstChar = argument.charAt(RANGE_FIRST_CHAR_INDEX);
        int lastChar = argument.charAt(RANGE_LAST_CHAR_INDEX);
        if(argument.charAt(RANGE_FIRST_CHAR_INDEX) > argument.charAt(RANGE_LAST_CHAR_INDEX)) {
            firstChar = argument.charAt(RANGE_LAST_CHAR_INDEX);
            lastChar = argument.charAt(RANGE_FIRST_CHAR_INDEX);
        }
        if (firstChar < MIN_LEGAL_CHAR || lastChar > MAX_LEGAL_CHAR) {
            throw new IllegalAddCommandException();
        }
        for (int charValue = firstChar; charValue <= lastChar; charValue++) {
            this.charMatcher.addChar((char) charValue);
        }
    }

    private void addSpaceCommand() {
        this.charMatcher.addChar(SPACE);
    }

    private void addAllCommand() {
        for (int charValue = MIN_LEGAL_CHAR; charValue <= MAX_LEGAL_CHAR; charValue++) {
            this.charMatcher.addChar((char)charValue);
        }
    }

    private void charsCommand() {
        for (char character: this.charMatcher.getSortedCharset()) {
            System.out.println(String.format(CHARSET_PRINTING_FORMAT, character));
        }
    }
}
