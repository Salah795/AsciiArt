package ascii_art;

public class Shell {

    private static final String RUN_PROMPT = ">>> ";
    private static final String EXIT_COMMAND = "exit";
    private static final int IMAGE_NAME_INDEX = 0;

    public Shell() {}

    public void run(String imageName) {
        System.out.println(RUN_PROMPT);
        String userInput = KeyboardInput.readLine();
        while (!userInput.equals(EXIT_COMMAND)) {
            //TODO continue implementation here...
            System.out.println(RUN_PROMPT);
            userInput = KeyboardInput.readLine();
        }
    }

    public static void main(String[] args) {
        Shell shell = new Shell();
        shell.run(args[IMAGE_NAME_INDEX]);
    }
}
