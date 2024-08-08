package ui;

import java.util.Scanner;

public class MainMenu {

    private final ChessClient client;

    public MainMenu(String serverUrl) {
        client = new ChessClient(serverUrl);
    }

    /**
     * Starts the chess client, displaying introductory text
     */
    public void run() {
        System.out.println( EscapeSequences.SET_TEXT_COLOR_GREEN + "♘ Lets Play Chess! ♖");
        System.out.print(EscapeSequences.RESET_TEXT_COLOR + client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.print(EscapeSequences.RESET_TEXT_COLOR + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println("\n" + EscapeSequences.SET_TEXT_COLOR_YELLOW + "See ya later!");
    }

    /**
     * Prints arrows to prompt the user for input
     */
    private void printPrompt() {
        System.out.print("\n" + EscapeSequences.SET_TEXT_COLOR_GREEN + ">>> ");
    }
}
