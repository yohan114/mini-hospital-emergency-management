package hospital.app;

import java.util.Scanner;

/**
 * Small helper around {@link Scanner} that keeps all input validation in one place.
 *
 * <p>Every read repeats the prompt until the value is acceptable, so a typing mistake can
 * never crash the menu with a {@code NumberFormatException}.</p>
 */
public class ConsoleInput {

    private final Scanner scanner;

    /**
     * When enabled, every line that is read is printed back. This is only used when the
     * program is driven from an input file (see {@code --echo} in Main) so that the saved
     * transcript shows the answers next to the prompts.
     */
    private boolean echoEnabled;

    public ConsoleInput(Scanner scanner) {
        this.scanner = scanner;
    }

    public void setEchoEnabled(boolean echoEnabled) {
        this.echoEnabled = echoEnabled;
    }

    /** Reads any whole number. */
    public int readInt(String prompt) {
        return readInt(prompt, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    /** Reads a whole number inside the given inclusive range. */
    public int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String line = nextLine().trim();
            try {
                int value = Integer.parseInt(line);
                if (value < min || value > max) {
                    System.out.println("  ! Please enter a number between " + min + " and " + max + ".");
                    continue;
                }
                return value;
            } catch (NumberFormatException ex) {
                System.out.println("  ! '" + line + "' is not a valid number. Please try again.");
            }
        }
    }

    /** Reads a line that must not be blank. */
    public String readText(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = nextLine().trim();
            if (!line.isEmpty()) {
                return line;
            }
            System.out.println("  ! This value cannot be empty. Please try again.");
        }
    }

    /** Reads a line, falling back to {@code defaultValue} when the user just presses Enter. */
    public String readText(String prompt, String defaultValue) {
        System.out.print(prompt);
        String line = nextLine().trim();
        return line.isEmpty() ? defaultValue : line;
    }

    /** Reads a yes/no answer. Anything starting with y or Y counts as yes. */
    public boolean readYesNo(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = nextLine().trim().toLowerCase();
            if (line.startsWith("y")) {
                return true;
            }
            if (line.startsWith("n")) {
                return false;
            }
            System.out.println("  ! Please answer with y or n.");
        }
    }

    /**
     * Reads the next line and stops the program cleanly if the input has run out, which
     * happens when the menu is fed from a script file that does not end with the exit
     * option.
     */
    private String nextLine() {
        if (!scanner.hasNextLine()) {
            System.out.println();
            System.out.println("  (no more input available - closing the system)");
            System.exit(0);
        }
        String line = scanner.nextLine();
        if (echoEnabled) {
            System.out.println(line);
        }
        return line;
    }
}
