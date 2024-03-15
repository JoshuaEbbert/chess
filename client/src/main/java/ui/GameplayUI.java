package ui;

import chess.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static ui.EscapeSequences.SET_BG_COLOR_BLACK;
import static ui.EscapeSequences.SET_TEXT_COLOR_WHITE;

public class GameplayUI {
    public void run(PrintStream out, Scanner scanner, ServerFacade server) { // TODO: add repl
        var game = new ChessGame();
        game.getBoard().resetBoard();
        showBoards(game);
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