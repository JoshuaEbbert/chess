package ui;

import chess.*;
import serverLogic.GameHandler;
import serverLogic.ServerFacade;
import serverLogic.WebSocketFacade;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class GameplayUI implements GameHandler {
    private static final String EXIT_COMMAND = "leave";
    private static final String STATE = "[GAMEPLAY]";

    private final String authorization;
    private final PrintStream out;
    private ChessGame game = new ChessGame();
    private final ChessGame.TeamColor teamColor;
    private final int gameID;

    public GameplayUI(String authorization, int gameID, ChessGame.TeamColor teamColor, PrintStream printer) {
        this.authorization = authorization;
        this.game.getBoard().resetBoard();
        this.gameID = gameID;
        this.teamColor = teamColor;
        this.out = printer;
    }
    public void run(Scanner scanner, ServerFacade server, WebSocketFacade webSocket) {
        String input = "";
        while (true) {
            out.print(STATE + " >>> ");
            String[] inputArray = scanner.nextLine().split(" ");
            out.print(ERASE_SCREEN);
            if (inputArray[0].equals("help")) {
                out.println("""
                        Commands:\s
                        \thelp - show available commands,\s
                        \tredraw - redraws the chess board,\s
                        \tleave - leave the game\s
                        \tmove <from> <to> <promotion piece (default is none)> - move a piece. Position given by <column letter><row number>. Promotion piece ignored unless for an eligible move.\s
                        \tresign - must confirm. Forfeit and end the game
                        \thighlight <position> - highlight all the possible moves for a piece at the given position. If there is no piece, nothing happens""");
            } else if (inputArray[0].equals("highlight") && inputArray.length == 2) {
                try {
                    ChessPosition pos = strToPos(inputArray[1]);
                    if (game.getBoard().getPiece(pos) != null) {
                        showBoard(game, teamColor, pos);
                    } else {
                        showBoard(game, teamColor);
                    }
                } catch (Exception e) {
                    out.println("Error: Invalid position. Give position as <column letter><row number>. E.g. e4");
                }
            } else if (inputArray[0].equals("redraw")) {
                showBoard(game, teamColor);
            } else if (inputArray[0].equals("move") && (inputArray.length == 3 || inputArray.length == 4)) {
                ChessPosition from = strToPos(inputArray[1]);
                ChessPosition to = strToPos(inputArray[2]);

                ChessPiece.PieceType promotionType = null;
                if (inputArray.length == 4) {
                    switch (inputArray[3].toLowerCase()) {
                        case "queen":
                            promotionType = ChessPiece.PieceType.QUEEN;
                            break;
                        case "rook":
                            promotionType = ChessPiece.PieceType.ROOK;
                            break;
                        case "bishop":
                            promotionType = ChessPiece.PieceType.BISHOP;
                            break;
                        case "knight":
                            promotionType = ChessPiece.PieceType.KNIGHT;
                            break;
                        default:
                            out.println("Invalid promotion piece. Options: queen, rook, bishop, knight");
                    }

                    if (promotionType == null) { // user entered invalid promotion piece
                        continue;
                    }
                }

                ChessMove move = new ChessMove(from, to, promotionType);
                webSocket.makeMove(authorization, gameID, move);

            } else if (inputArray[0].equals("resign")) {
                if (teamColor == null) {
                    out.println("Error: Cannot resign from a game you are not playing in.");
                    continue;
                }

                out.println("Are you sure you want to resign? (y/n)");
                if (scanner.nextLine().equals("y")) {
                    if (webSocket.resign(authorization, gameID)) {
                        out.println("Leaving game now.");
                        webSocket.leaveGame(authorization, gameID, teamColor); // not checking for success as the user is already committed to leaving
                        break;
                    } else {
                        out.println("Error resigning. Please try again.");
                    }
                }
            } else if (!inputArray[0].equals(EXIT_COMMAND)) {
                out.println("Error: Invalid command. Type 'help' to see available commands.");
            } else if (webSocket.leaveGame(authorization, gameID, teamColor)) { // input is exit_command and leave was successful
                break;
            }
        }
    }

    public void updateGame(ChessGame game) {
        this.game = game;
        showBoard(game, teamColor);
        out.print(STATE + " >>> ");
    }
    public void printMessage(String message) {
        out.println(message);
    }

    private static void showBoard(ChessGame game, ChessGame.TeamColor color) {
        if (color == null) { color = ChessGame.TeamColor.WHITE; }
        printLetters(color);
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
        printLetters(color);
    }

    // Overloading showBoard to implement highlighting
    private static void showBoard(ChessGame game, ChessGame.TeamColor color, ChessPosition highlight) {
        HashSet<ChessPosition> validMoves = new HashSet<>();
        for (ChessMove move: game.validMoves(highlight)) {
            validMoves.add(move.getEndPosition());
        }

        printLetters(color);
        if (color == ChessGame.TeamColor.BLACK) {
            for (int row = 1; row < 9; row++) {
                System.out.print(row + " ");
                for (int col = 8; col > 0; col--) {
                    printWithHighlight(game, highlight, validMoves, row, col);
                }
                System.out.println("| " + row);
            }
        } else { // White on bottom is default
            for (int row = 8; row > 0; row--) {
                System.out.print(row + " ");
                for (int col = 1; col < 9; col++) {
                    printWithHighlight(game, highlight, validMoves, row, col);
                }
                System.out.println("| " + row);
            }
        }
        printLetters(color);
    }

    private static void printWithHighlight(ChessGame game, ChessPosition highlight, HashSet<ChessPosition> validMoves, int row, int col) {
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

    private static void printLetters(ChessGame.TeamColor color) {
        System.out.print("\n   ");
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

    private static ChessPosition strToPos(String str) {
        return new ChessPosition(Integer.parseInt(str.substring(1)), str.charAt(0) - 96);
    }

    private static String getPieceStr(ChessGame game, ChessPosition pos) {
        ChessPiece piece = game.getBoard().getPiece(pos);
        return piece == null ? "   " : " " + piece.toChar() + " ";
    }
}