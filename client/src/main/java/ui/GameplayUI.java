package ui;

import chess.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class GameplayUI {
    private final String EXIT_COMMAND = "quit";
    private final String STATE = "[GAMEPLAY]";
    public void run(PrintStream out, Scanner scanner, ServerFacade server) {
        var game = new ChessGame();
        game.getBoard().resetBoard();
        showBoards(game);

        String input = "";
        while (!input.equals(EXIT_COMMAND)) {
            out.print(STATE + " >>> ");
            input = scanner.nextLine();
            String[] input_array = input.split(" ");
            out.print(ERASE_SCREEN);
            if (input_array[0].equals("help")) {
                out.println("Commands: \n\thelp - show available commands, \n\tquit - exit game");
            } else if (!input.equals(EXIT_COMMAND)) {
                showBoards(game);
                out.println("Error: Invalid command. Type 'help' to see available commands.");
            }
        }
    }

    private static void showBoards(ChessGame game) {
        // print white top
        printTop();
        for (int row = 8; row > 0; row--) {
            System.out.print(row + " ");
            for (int col = 1; col < 9; col++) {
                System.out.print("|");
                ChessPiece piece = game.getBoard().getPiece(new ChessPosition(row, col));
                if (piece == null) {
                    System.out.print("   ");
                } else {
                    System.out.print(" " + piece.toChar() + " ");
                }
            }
            System.out.println("| " + row);
        }
        printBottom();

        // buffer space
        System.out.println();
        System.out.println();

        // print white bottom
        printTop();
        for (int row = 8; row > 0; row--) {
            System.out.print(row + " ");
            for (int col = 1; col < 9; col++) {
                System.out.print("|");
                ChessPiece piece = getFlippedPiece(game, row, col);
                if (piece == null) {
                    System.out.print("   ");
                } else {
                    System.out.print(" " + piece.toChar() + " ");
                }
            }
            System.out.println("| " + row);
        }
        printBottom();
    }

    private static void printTop() {
        System.out.print("   ");
        for (int col = 1; col < 9; col++) {
            System.out.print(" " + (char) (col + 96) + "  ");
        }
        System.out.println();
    }

    private static void printBottom() {
        System.out.print("   ");
        for (int col = 1; col < 9; col++) {
            System.out.print(" " + (char) (col + 96) + "  ");
        }
        System.out.println();
    }

    private static ChessPiece getFlippedPiece(ChessGame game, int row, int col) {
        return game.getBoard().getPiece(new ChessPosition(9 - row, 9 - col));
    }
}