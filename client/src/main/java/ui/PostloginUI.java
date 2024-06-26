package ui;

import chess.ChessGame;
import serverLogic.GameHandler;
import serverLogic.ServerFacade;
import serverLogic.WebSocketFacade;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

public class PostloginUI {
    private static final String EXIT_COMMAND = "logout";
    private static final String STATE = "[LOGGED IN]";
    private String username;
    private String authorization;

    private ArrayList<Map<String, Object>> games; // Map<String, Object> represents a game

    public PostloginUI(String username, String authorization) {
        this.username = username;
        this.authorization = authorization;
    }
    public void run(PrintStream out, Scanner scanner, ServerFacade server) {
        String input = "";
        boolean exit = false;
        while (!exit) {
            out.print(STATE + " as " + username + " >>> ");
            input = scanner.nextLine();

            String[] inputArray = input.split(" ");

            if (inputArray[0].equals("help")) {
                out.println("Commands: ");
                for (String option: new String[] {
                        "create <GAME_NAME> - create a new game",
                        "list - list all available games",
                        "join <GAME_ID> [WHITE|BLACK|<empty>] - join a game. Adding no color joins as an observer",
                        "observe <GAME_ID> - observe a game",
                        "logout - when you are done playing",
                        "help - show possible commands"
                }) {
                    out.println("\t" + option);
                }
            } else if (inputArray[0].equals("create") && inputArray.length == 2) {
                try {
                    if (games == null) {
                        games = server.listGames(authorization);
                    }

                    int gameID = server.createGame(authorization, inputArray[1]);
                    games.add(Map.of("gameID", (double) gameID, "gameName", inputArray[1]));
                    out.println("Successfully created game #" + games.size() + " " + inputArray[1]);
                } catch (Exception e) {
                    out.println(e.getMessage());
                }
            } else if (inputArray[0].equals("list") && inputArray.length == 1) {
                listGames(out, server);
            } else if (inputArray[0].equals("join") && inputArray.length == 2) {
                observeGame(inputArray, out, scanner, server);
            } else if (inputArray[0].equals("join") && inputArray.length == 3) {
                joinGame(inputArray, out, scanner, server);
            } else if (inputArray[0].equals("observe") && inputArray.length == 2) {
                observeGame(inputArray, out, scanner, server);
            } else if (inputArray[0].equals(EXIT_COMMAND)) { // logout
                try {
                    server.logout(authorization);
                    out.println("Successfully logged out!");
                    exit = true;
                } catch (Exception e) {
                    out.println(e.getMessage());
                }
            } else if (!input.equals(EXIT_COMMAND)) {
                out.println("Error: Invalid command. Type 'help' to see available commands.");
            }
        };
    }

    private void joinGame(String[] inputArray, PrintStream out, Scanner scanner, ServerFacade server) {
        try {
            int gameID = getGameID(server, inputArray, out);
            String color = inputArray[2];

            if (!color.equals("WHITE") && !color.equals("BLACK")) {
                throw new Exception("Invalid color");
            }

            server.joinGame(authorization, color, gameID);
            ChessGame.TeamColor teamColor;

            teamColor = color.equals("WHITE") ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;
            GameplayUI gameUI = new GameplayUI(authorization, gameID, teamColor, out);
            WebSocketFacade webSocket = new WebSocketFacade(8080, authorization, (GameHandler) gameUI);
            webSocket.connect();
            webSocket.joinPlayer(gameID, teamColor);
            gameUI.run(scanner, server, webSocket);
        } catch (Exception e) {
            out.println(e.getMessage());
        }
    }

    private void observeGame(String[] inputArray, PrintStream out, Scanner scanner, ServerFacade server) {
        try {
            int gameID = getGameID(server, inputArray, out);
            server.joinGame(authorization, null, gameID);
            GameplayUI game = new GameplayUI(authorization, gameID, null, out); // White passed as default color to set board display
            WebSocketFacade webSocket = new WebSocketFacade(8080, authorization, (GameHandler) game);
            webSocket.connect();
            webSocket.joinObserver(gameID);
            game.run(scanner, server, webSocket);
        } catch (Exception e) {
            out.println(e.getMessage());
        }
    }

    private void listGames(PrintStream out, ServerFacade server) {
        try {
            games = server.listGames(authorization);
            if (!games.isEmpty()) {
                out.println("Available games:");
                for (int i = 0; i < games.size(); i++) {
                    Map<String, Object> game = games.get(i);
                    String blackUsername = game.get("blackUsername") == null ? "None" : (String) game.get("blackUsername");
                    String whiteUsername = game.get("whiteUsername") == null ? "None" : (String) game.get("whiteUsername");
                    out.println("\t" + (i + 1) + ". " + game.get("gameName"));
                    out.println("\t\tWHITE: " + whiteUsername + " BLACK: " + blackUsername);
                }
            } else {
                out.println("No available games.");
            }
        } catch (Exception e) {
            out.println(e.getMessage());
        }
    }

    private int getGameID(ServerFacade server, String[] inputArray, PrintStream out) throws Exception {
        if (games == null) {
            games = server.listGames(authorization);
        }

        try {
            Integer.parseInt(inputArray[1]);
        } catch (Exception e) {
            throw new Exception("To join please specify a game number");
        }

        if (games == null || Integer.parseInt(inputArray[1]) < 1 || Integer.parseInt(inputArray[1]) > games.size()) {
            throw new Exception("Invalid game ID");
        }

        return (int) ((Double) games.get(Integer.parseInt(inputArray[1]) - 1).get("gameID")).doubleValue();
    }
}
