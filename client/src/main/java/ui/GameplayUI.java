package ui;

import chess.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class GameplayUI {
    private final String EXIT_COMMAND = "leave";
    private final String STATE = "[GAMEPLAY]";

    private final String authorization;

    public GameplayUI(String authorization) {
        this.authorization = authorization;
    }
    public void run(PrintStream out, Scanner scanner, ServerFacade server, WebSocketFacade webSocket) {

        // placeholder code; need to retrieve board and color
        var game = new ChessGame();
        game.getBoard().resetBoard();
        ChessGame.TeamColor color = ChessGame.TeamColor.BLACK;
        game.getBoard().addPiece(new ChessPosition(3, 3), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
        showBoard(game, color);

        String input = "";
        while (!input.equals(EXIT_COMMAND)) {
            out.print(STATE + " >>> ");
            String[] input_array = scanner.nextLine().split(" ");
            out.print(ERASE_SCREEN);
            if (input_array[0].equals("help")) {
                out.println("""
                        Commands:\s
                        \thelp - show available commands,\s
                        \tredraw - redraws the chess board,\s
                        \tleave - leave the game\s
                        \tmove <from> <to> - move a piece. Position given by <column letter><row number>\s
                        \tresign - must confirm. Forfeit and end the game
                        \thighlight <position> - highlight all the possible moves for a piece at the given position. If there is no piece, nothing happens""");
            } else if (input_array[0].equals("highlight") && input_array.length == 2) {
                try {
                    ChessPosition pos = strToPos(input_array[1]);
                    if (game.getBoard().getPiece(pos) != null) {
                        showBoard(game, color, pos);
                    } else {
                        showBoard(game, color);
                    }
                } catch (Exception e) {
                    out.println("Error: Invalid position. Give position as <column letter><row number>. E.g. e4");
                }
            } else if (input_array[0].equals("redraw")) {
                showBoard(game, color);
//            } else if (input_array[0].equals("move") && input_array.length == 3) {
//                ChessPosition from = new ChessPosition(input_array[1]);
//                ChessPosition to = new ChessPosition(input_array[2]);
//
            } else if (input_array[0].equals("resign")) {
                out.println("Are you sure you want to resign? (y/n)");
                if (scanner.nextLine().equals("y")) {
                    out.println("You have resigned. Game over.");
                    //TODO: end game
                    input = EXIT_COMMAND;
                }
            } else if (!input_array[0].equals(EXIT_COMMAND)) {
                out.println("Error: Invalid command. Type 'help' to see available commands.");
            }
        }
    }

    private static void showBoard(ChessGame game, ChessGame.TeamColor color) {
        printTop(color);
        if (color == ChessGame.TeamColor.BLACK) {
            for (int row = 1; row < 9; row++) {
                System.out.print(row + " ");
                for (int col = 8; col > 0; col--) {
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
        } else { // White on bottom is default
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
        }
        printBottom(color);
    }

    // Overloading showBoard to implement highlighting
    private static void showBoard(ChessGame game, ChessGame.TeamColor color, ChessPosition highlight) {
        HashSet<ChessPosition> validMoves = new HashSet<>();
        for (ChessMove move: game.validMoves(highlight)) {
            validMoves.add(move.getEndPosition());
        }

        printTop(color);
        if (color == ChessGame.TeamColor.BLACK) {
            for (int row = 1; row < 9; row++) {
                System.out.print(row + " ");
                for (int col = 8; col > 0; col--) {
                    System.out.print("|");
                    ChessPosition pos = new ChessPosition(row, col);
                    if (validMoves.contains(pos)) {
                        System.out.print(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_BOLD + getPieceStr(game, pos) + "\u001B[0m");
                    } else if (highlight.equals(pos)) {
                        System.out.print(SET_BG_COLOR_RED + getPieceStr(game, pos) + "\u001B[0m");
                    } else {
                        System.out.print(getPieceStr(game, pos));
                    }
                }
                System.out.println("| " + row);
            }
        } else { // White on bottom is default
            for (int row = 8; row > 0; row--) {
                System.out.print(row + " ");
                for (int col = 1; col < 9; col++) {
                    System.out.print("|");
                    ChessPosition pos = new ChessPosition(row, col);
                    if (validMoves.contains(pos)) {
                        System.out.print(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_BOLD + getPieceStr(game, pos) + "\u001B[0m");
                    } else if (highlight.equals(pos)) {
                        System.out.print(SET_TEXT_BLINKING + getPieceStr(game, pos) + "\u001B[0m");
                    } else {
                        System.out.print(getPieceStr(game, pos));
                    }
                }
                System.out.println("| " + row);
            }
        }
        printBottom(color);
    }

    private static void printTop(ChessGame.TeamColor color) {
        System.out.print("   ");
        if (color == ChessGame.TeamColor.BLACK) {
            for (int col = 8; col > 0; col--) {
                System.out.print(" " + (char) (col + 96) + "  ");
            }
        } else { // White on bottom is default
            for (int col = 1; col < 9; col++) {
                System.out.print(" " + (char) (col + 96) + "  ");
            }
        }
        System.out.println();
    }

    private static void printBottom(ChessGame.TeamColor color) {
        System.out.print("   ");
        if (color == ChessGame.TeamColor.BLACK) {
            for (int col = 8; col > 0; col--) {
                System.out.print(" " + (char) (col + 96) + "  ");
            }
        } else {
            for (int col = 1; col < 9; col++) {
                System.out.print(" " + (char) (col + 96) + "  ");
            }
        }
        System.out.println();
    }

    private static ChessPosition strToPos(String str) {
        return new ChessPosition(Integer.parseInt(str.substring(1)), str.charAt(0) - 96);
    }

    private static String getPieceStr(ChessGame game, ChessPosition pos) {
        ChessPiece piece = game.getBoard().getPiece(pos);
        return piece == null ? "   " : " " + piece.toChar() + " ";
    }
}