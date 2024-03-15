package ui;

import requests.JoinGameRequest;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

import static ui.EscapeSequences.ERASE_SCREEN;

public class PostloginUI {
    private final String EXIT_COMMAND = "logout";
    private final String STATE = "[LOGGED IN]";
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

            String[] input_array = input.split(" ");

            if (input_array[0].equals("help")) {
                out.println("Commands: ");
                for (String option: new String[] {
                        "create <GAME_NAME> - create a new game",
                        "list - list all available games",
                        "join <GAME_ID> [WHITE|BLACK|<empty>] - join a game",
                        "observe <GAME_ID> - observe a game",
                        "logout - when you are done playing",
                        "help - show possible commands"
                }) {
                    out.println("\t" + option);
                }
            } else if (input_array[0].equals("create") && input_array.length == 2) {
                try {
                    int gameID = server.createGame(authorization, input_array[1]); // TODO: add to games list?
                    out.println("Successfully created game " + input_array[1]);
                } catch (Exception e) {
                    out.println("Error: " + e.getMessage());
                }
            } else if (input_array[0].equals("list") && input_array.length == 1) {
                listGames(out, server);
            } else if (input_array[0].equals("join") && (input_array.length == 3 || input_array.length == 2)) {
                joinGame(input_array, out, scanner, server);
            } else if (input_array[0].equals("observe") && input_array.length == 2) {
                observerGame(input_array, out, scanner, server);
            } else if (input_array[0].equals(EXIT_COMMAND)) { // logout
                try {
                    server.logout(authorization);
                    out.println("Successfully logged out!");
                    exit = true;
                } catch (Exception e) {
                    out.println("Error: " + e.getMessage());
                }
            } else if (!input.equals(EXIT_COMMAND)) {
                out.println("Error: Invalid command. Type 'help' to see available commands.");
            }
        };
    }

    private void joinGame(String[] input_array, PrintStream out, Scanner scanner, ServerFacade server) {
        try {
            int gameID = (int) ((Double) games.get(Integer.parseInt(input_array[1])).get("gameID")).doubleValue();            String color = input_array.length == 3 ? input_array[2] : null;
            server.joinGame(authorization, color, gameID);
            out.println("Successfully joined game!");
            GameplayUI game = new GameplayUI();
            game.run(out, scanner, server);
        } catch (Exception e) {
            out.println("Error: " + e.getMessage());
        }
    }

    private void observerGame(String[] input_array, PrintStream out, Scanner scanner, ServerFacade server) {
        try {
            int gameID = (int) games.get(Integer.parseInt(input_array[1])).get("gameID");
            server.joinGame(authorization, null, gameID);
            out.println("Successfully observing game!");
            GameplayUI game = new GameplayUI();
            game.run(out, scanner, server);
        } catch (Exception e) {
            out.println("Error: " + e.getMessage());
        }
    }

    private void listGames(PrintStream out, ServerFacade server) {
        try {
            games = server.listGames(authorization);
            if (!games.isEmpty()) {
                out.println("Available games:");
                for (int i = 0; i < games.size(); i++) {
                    Map<String, Object> game = games.get(i);
                    out.println("\t" + (i + 1) + ". " + game.get("gameName")); // TODO: add player info
                }
            } else {
                out.println("No available games.");
            }
        } catch (Exception e) {
            out.println("Error: " + e.getMessage());
        }
    }
}
